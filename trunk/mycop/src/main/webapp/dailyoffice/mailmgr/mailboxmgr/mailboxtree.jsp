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
	//var tree = new WebFXLoadTree("部门",  context+"/dailyoffice/targetmng/treemonthtarget.do?action=doTreeData","javascript:toRight('/dailyoffice/targetmng/listmonthtarget.do')",null, null, null, null,'0');
	var tree = new WebFXLoadTree("所有部门",  context+"/dailyoffice/mailmgr/mailboxmgr/mailboxtreemgr.do?action=doTreeData", "javascript:toRight('/dailyoffice/mailmgr/mailboxmgr/mailboxmgrlist.do?nodeid=false')",null, null, null, null,'0');
	try{
		tree.expand();
	} catch(e) {
	}

	document.write(tree);
	/*try{
		document.body.onmousedown=function(){window.parent.window.parent.hideAllMenu();	}
	} catch(e) {
	}*/
</script>
 
</body>
</html>
