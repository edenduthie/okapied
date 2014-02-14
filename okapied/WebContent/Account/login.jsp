<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="org.springframework.security.web.WebAttributes" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

    <s:include value="/header.jsp"></s:include>
    <div id="login-page">
        <div id="login-page-top">
            <div class="top-right"></div>
            <div class="top-left"></div>
            <div class="top-center"></div>
        </div>
        <div class="login-content">
            <div class="login-text">
		    <h1>Login</h1>
		    <div class="message">
		    <% 
		        Object error = session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		        if( error != null )
		        {
		        	Exception e = (Exception) error;
		        	out.println("Your login attempt was not successful, please try again<br>");
		        	//out.println("Reason: "+e.getMessage());
		        }
		    %>
		    <s:property value="message" />
		    </div>
		    <div id="login-center">
		    <form name="f" action="<%=request.getContextPath()%>/j_spring_security_check" method="POST">
		        <div class="input-row"><div class="label-text">username</div><input id='j_username' class="input-short" type='text' name='j_username' value='' /></div>
		        <div class="clearfloat"></div>
		        <div class="input-row"><div class="label-text">password</div><input class="input-short" type='password' name='j_password'></div>
		        <!-- <div class="input-row"><div class="input-right"><input type="checkbox" name="_spring_security_remember_me">&nbsp;&nbsp;Don't ask for my password for two weeks</div></div>  -->
		        <div class="input-row"><div class="input-right"><input type="image" src="<%=request.getContextPath()%>/images/log_in_button.png" alt="Log In" title="Log In" /></div></div>
		        <div class="input-row"><div class="label-text">or</div><a href="<%=request.getContextPath()%>/Account/Account/create">Create an Account</a></div>
		    </form>
		    </div>
		    </div>
		</div>
        <div id="login-bottom">
            <div class="bottom-right"></div>
            <div class="bottom-left"></div>
            <div class="bottom-center"></div>
        </div>
    </div>
 <script>
 $(document).ready(function(){
	 $('#j_username').focus();
 });
 </script>
 <s:include value="/footer.jsp"></s:include>
</body>
</html>