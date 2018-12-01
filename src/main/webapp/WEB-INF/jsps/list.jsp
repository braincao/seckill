<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="common/taglib.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%
    String path = request.getContextPath();
    String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<html>
<head>
    <title>秒杀列表页</title>

    <!--静态包含jsp，将所引的jsp部分会合并过来-->
    <%@include file="common/head.jsp"%>

</head>

<body>

    <!--页面显示部分-->
    <div class="container">
        <div class="panel panel-default">
            <div class="panel-heading text-center">
                <h2>秒杀列表</h2>
            </div>
            <div class="panel-body">
                <table class="table table-hover">
                    <thead>
                        <tr>
                            <th>名称</th>
                            <th>库存</th>
                            <th>开始时间</th>
                            <th>结束时间</th>
                            <th>创建时间</th>
                            <th>详情页</th>
                        </tr>
                    </thead>

                    <tbody>
                        <c:forEach items="${list}" var="seckill">
                            <tr>
                                <td>${seckill.name}</td>
                                <td>${seckill.number}</td>
                                <td>
                                    <fmt:formatDate value="${seckill.startTime}" pattern="yyyy-MM--dd HH:mm:ss"/>
                                </td>
                                <td>
                                    <fmt:formatDate value="${seckill.endTime}" pattern="yyyy-MM--dd HH:mm:ss"/>
                                </td>
                                <td>
                                    <fmt:formatDate value="${seckill.createTime}" pattern="yyyy-MM--dd HH:mm:ss"/>
                                </td>
                                <td>
                                    <a class="btn btn-info" href="/seckill/${seckill.seckillId}/detail">进入秒杀详情页</a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>

                </table>
            </div>
        </div>
    </div>

</body>


</html>
