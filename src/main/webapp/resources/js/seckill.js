//存放主要交互逻辑js代码
//javascript模块化封装，不要写成一坨
var seckill = {
    //封装所有秒杀相关的url
    URL:{
        now:function(){
            return "/seckill/time/now"
        },

        exposer:function(seckillId){
            return "/seckill/" + seckillId + "/exposer";
        },

        execution:function(seckillId, md5){
            return "/seckill/" + seckillId + "/" + md5 + "/execution";
        }
    },

    //秒杀开始时的秒杀逻辑:获取秒杀地址，控制显示逻辑，执行秒杀
    handlerSeckill:function(seckillId, node){
        node.hide().html("<button class='btn btn-primary btn-lg' id='killBtn'>开始秒杀</button>");//隐藏按钮
        //通过ajaxPOST请求获取秒杀接口地址
        $.post(seckill.URL.exposer(seckillId), {}, function(result){
            if(result && result["success"]){
                var exposer = result["data"];
                if(exposer["exposed"]){
                    //开启秒杀
                    //获取秒杀地址
                    var md5 = exposer["md5"];
                    var killUrl = seckill.URL.execution(seckillId, md5);//拿到服务器返回的秒杀接口地址
                    console.log("killUrl: " + killUrl);//输出到控制台
                    //用one绑定而不是click的好处就是：只绑定一次点击事件
                    // 防止一个用户不停的点秒杀，都发送服务端造成崩
                    $("#killBtn").one("click",function(){
                        //执行秒杀请求
                        //1.点击后按钮变灰色，禁用按钮
                        $(this).addClass("disabled");
                        //2.发送ajaxPOST请求，执行秒杀
                        $.post(killUrl,{},function(result){
                            if(result && result["success"]){
                                var killResult = result["data"];
                                var state = killResult["state"];
                                var stateInfo = killResult["stateInfo"];
                                //3.显示秒杀结果
                                node.html("<span class='label label-success'>" + stateInfo + "</span>");
                            }
                        });
                    });
                    node.show();
                }
            }
            else{
                console.log("result: " + result);
            }
        });
    },

    //验证手机号
    validatePhone:function(phone){
      if(phone && phone.length===11 && !isNaN(phone)){
          return true;
      }else{
          return false;
      }
    },

    //倒计时的时间判断
    countdown: function(seckillId, nowTime, startTime, endTime){
        var seckillBox = $("#seckill-box");
        //时间判断
        if(nowTime>endTime){
            //秒杀结束
            seckillBox.html("秒杀结束!");
        }
        else if(nowTime<startTime){
            //秒杀未开始，计时事件绑定，用countdown插件倒计时
            var killTime = new Date(startTime+1000);
            seckillBox.countdown(killTime,function(event){//countdown插件的函数
                //时间格式
                var format = event.strftime("秒杀倒计时：%D天 %H时 %M分 %S秒");
                seckillBox.html(format);
                /*倒计时结束后回调事件*/
            }).on("finish.countdown", function(){
                //获取秒杀地址，控制显示逻辑，执行秒杀
                seckill.handlerSeckill(seckillId, seckillBox);
            });
        }
        else{
            //秒杀开始
            seckill.handlerSeckill(seckillId, seckillBox);
        }
    },

    //详情页秒杀逻辑
    detail:{
        //详情页初始化
        init:function(params){
            //详情页初始化：手机验证和登录 + 计时交互
            //在cookie中查找手机号
            var killPhone = $.cookie("killPhone");
            //1.验证手机号
            if(!seckill.validatePhone(killPhone)){
                //手机号不存在，显示弹出层，开始绑定phone
                var killPhoneModal = $("#killPhoneModal");
                killPhoneModal.modal({
                    show:true,
                    backdrop:"static", //禁止位置关闭:点别的地方关不掉
                    keyboard:false //关闭键盘事件:按esc关不掉
                });
                //监听回车事件:回车也同样进行下面的click
                document.onkeydown = function(e){
                    if(e.keyCode === 13){
                        $("#killPhoneButton").click();
                    }
                };
                $("#killPhoneButton").click(function(){
                    var inputPhone = $("#killPhoneKey").val();
                    console.log("inputPhone" + inputPhone);
                    if(seckill.validatePhone(inputPhone)){
                        //手机号写入cookie，名字为killPhone，值为用户输入的正确手机号，有效期为7天，只在/seckill路径下有效
                        $.cookie("killPhone", inputPhone, {expires:7,path:"/seckill"});
                        //刷新页面
                        window.location.reload();
                    }
                    else{//手机号填写错误，这个过程先hide再show，这样用户看不到中间渲染的过程，更好的体验
                        $("#killPhoneMessage").hide().html("<lable class='label label-danger'>手机号填写错误!</lable>").show(300);
                    }
                })
            }
            //2.已经登录，开始计时交互
            //通过ajaxGET请求获取服务器时间/time/now，ajax请求返回的数据是json格式的result
            var seckillId = params["seckillId"];
            var startTime = params["startTime"];
            var endTime = params["endTime"];
            $.get(seckill.URL.now(), {}, function(result){
                if(result && result["success"]){
                    var nowTime = result["data"];//拿到服务器时间
                    //时间判断，计时交互
                    seckill.countdown(seckillId,nowTime,startTime, endTime);
                }
                else{
                    console.log("result: " + result);
                }
            });
        }
    }
};