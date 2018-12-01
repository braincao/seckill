<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="common/taglib.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%
    String path = request.getContextPath();
    String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<html>
<head>
    <title>秒杀详情页</title>

    <!--静态包含jsp，将所引的jsp部分会合并过来-->
    <%@include file="common/head.jsp"%>
</head>

<body>

    <div class="container">
        <div class="panel panel-default text-center">
            <div class="panel-heading">
                <h1>${seckill.name}</h1>
            </div>
            <div class="panel-body">
                <h2 class="text-danger">
                    <!--显示time图标-->
                    <span class="glyphicon glyphicon-time"></span>
                    <!--展示倒计时-->
                    <span class="glyphicon" id="seckill-box"></span>
                </h2>
            </div>
        </div>
    </div>

    <!--登录弹出层，输入电话-->
    <div id="killPhoneModal" class="modal fade">
        <div class="modal-dialog">
            <div class="modal-content">

                <div class="modal-header">
                    <h3 class="modal-title text-center">
                        <span class="glyphicon glyphicon-phone"></span>
                        秒杀前请输入用户手机号
                    </h3>
                </div>

                <div class="modal-body">
                    <div class="row">
                        <div class="col-xs-8 col-xs-offset-2">
                            <input type="text" name="killPhone" autofocus class="form-control" id="killPhoneKey" placeholder="填手机号"/>
                        </div>
                    </div>
                </div>

                <div class="modal-footer">
                    <!--验证信息-->
                    <span id="killPhoneMessage" class="glyphicon"></span>
                    <button type="button" id="killPhoneButton" class="btn btn-success">
                        <span class="glyphicon glyphicon-phone"></span>
                        Submit
                    </button>
                </div>

            </div>
        </div>
    </div>

</body>
<!--引入的插件用cdn链接:cookie插件、倒计时插件-->
<script type="text/javascript" src="https://cdn.bootcss.com/jquery-cookie/1.4.1/jquery.cookie.min.js"></script>
<script type="text/javascript" src="https://cdn.bootcss.com/jquery.countdown/2.2.0/jquery.countdown.min.js"></script>

<script type="text/javascript" src="<%=path%>/resources/js/seckill.js"></script>
<script type="text/javascript">
    $(function(){
        //使用EL表达式传入参数，初始化页面
        seckill.detail.init({
            seckillId: ${seckill.seckillId},
            startTime: ${seckill.startTime.time}, //startTime是date类型，转成毫秒
            endTime: ${seckill.endTime.time}
        });
    });
</script>
</html>
