<%@ page contentType="text/html; charset=GB2312" %>
<%@ taglib uri="/WEB-INF/taglib/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/taglib/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/taglib/struts-logic.tld" prefix="logic" %>
<%
        response.setHeader("pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0L);%>
<%	String context =request.getContextPath();%>

<script type="text/javascript">

	var context = "<%=context%>";
	//var rti;
	var tree = new WebFXLoadTree("��������",  context+"/infoservice/typetree.do?action=doStaticTreeData","javascript:toRight('/infoservice/typelist.do?')",null, null, null, null,'0');
	try{
		tree.expand();
	} catch(e) {
	}

	document.write(tree);
</script>
 
</body>
</html>