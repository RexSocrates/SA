<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="">
<meta name="author" content="">
<title>通貨商進貨管理系統</title>

<!-- Bootstrap Core CSS -->
<link href="resources/vendor/bootstrap/css/bootstrap.min.css"
	rel="stylesheet">

<!-- MetisMenu CSS -->
<link href="resources/vendor/metisMenu/metisMenu.min.css"
	rel="stylesheet">

<!-- Custom CSS -->
<link href="resources/dist/css/sb-admin-2.css" rel="stylesheet">

<!-- Morris Charts CSS -->
<link href="resources/vendor/morrisjs/morris.css" rel="stylesheet">

<!-- Custom Fonts -->
<link href="resources/vendor/font-awesome/css/font-awesome.min.css"
	rel="stylesheet" type="text/css">

<link href="resources/dist/css/style.css" rel="stylesheet"
	type="text/css">
<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
<!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
        <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
</head>
<body>
	<div class="container">
        <div class="row">
            <div class="col-md-4 col-md-offset-4">
                <div class="login-panel panel panel-default">
                    <div class="panel-heading">
                        <h3 class="panel-title">登入</h3>
                    </div>
                    <div class="panel-body">
                        <fieldset>
                            <div class="form-group">
                                <center>
                                    <!-- <input type="text" name=""> -->
                                    <h1><i class="fa fa-barcode fa-fw"></i>請掃描工作證</h1>
                                    <br>
                                    <img src="resources/img/barcode-scanner.png">
                                    <%-- <c:if test="${param.error != null}">
							            <h2>Username or password wrong!</h2>
							        </c:if> --%>
                                </center>
                            </div>
                            <form action="" method="post">
                                <input id="password" type="hidden" name="userID" value="">
                                <button type="submit" name="Button1" id="Button1" style="display:none"></button> 
                            </form>
                        </fieldset>
                    </div>
                </div>
            </div>
        </div>
    </div>

	<!-- jQuery -->
	<script src="resources/vendor/jquery/jquery.min.js"></script>

	<!-- Bootstrap Core JavaScript -->
	<script src="resources/vendor/bootstrap/js/bootstrap.min.js"></script>

	<!-- Metis Menu Plugin JavaScript -->
	<script src="resources/vendor/metisMenu/metisMenu.min.js"></script>

	<!-- Morris Charts JavaScript -->
	<script src="resources/vendor/raphael/raphael.min.js"></script>
	<script src="resources/vendor/morrisjs/morris.min.js"></script>
	<script src="resources/data/morris-data.js"></script>

	<!-- Custom Theme JavaScript -->
	<script src="resources/dist/js/sb-admin-2.js"></script>

	<script>
        $(document).ready(function() {
            var barcode="";
            $(document).keydown(function(e) {

                var code = (e.keyCode ? e.keyCode : e.which);
                console.log("code:"+code);
                if(code==13){// Enter key hit
                    document.getElementById("password").value = barcode;
                    $("#Button1").click();
                }
                else{
                    barcode=barcode+String.fromCharCode(code);
                }
                if(code == 32){// space key hit
                    var cc = document.getElementById('password').value;
                    console.log("password:"+cc);
                }
            });

        });

        document.onkeydown = function (e) {
             return false;
        }
    </script>
</html>