<%@ page contentType="text/html; charset=GB2312"%>
<%@ page language="java" %>
<%@ taglib uri="/WEB-INF/taglib/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/taglib/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/taglib/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/taglib/gever-goa.tld" prefix="goa" %>
<%String context =request.getContextPath();%>
<script language="javascript">
<logic:present name="ErrorMsg" scope="request" >
alert('<bean:write name="ErrorMsg" scope="request"/>')
</logic:present>
</script>
<html:form action="/admin/editnation" method="post">
<div align="center">
  <center>
    <br>  
	<table  align="center" width="500"  border="0" cellspacing="0" cellpadding="0">
	<tr> 
      <td align="center" colspan="2"><span class="TableTitleText">民族信息</span></td>
      </tr>
	<tr>
	<td align="center">
		<table width="98%" align="center" border="0" cellspacing="0" cellpadding="0">
		  <tr> 
			<td>&nbsp;<html:hidden name="NationForm" property="actionType"/></td>
		  </tr>
	   
		  <tr class="listcellrow" height="23" >
				<td width="86" nowrap class="InputLabelCell">民族代码:</td>
				<td ><html:text styleClass="input2" property="vo.national_code" size="40" validatetype="notempty"  msg="民族代码不能为空！" maxlength="4"/><font color="red">*</font></td>
			</tr>
			<tr class="listcellrow"  height="23" >
				<td width="86" nowrap class="InputLabelCell">民族名称:</td>
				<td ><html:text styleClass="input2" property="vo.national_name" size="40" validatetype="notempty" msg="民族名称不能为空！"  maxlength="10"/><font color="red">*</font></td>
			</tr>
			<tr class="listcellrow">
			<td width="86"  nowrap valign="top"  class="InputLabelCell">备&nbsp;&nbsp;&nbsp;注：</td>
				 <td ><html:textarea property="vo.remarks" cols="42" rows="4" styleClass="input2" maxlength="30"/></td>
		  </tr>
		  <tr>
			<td>&nbsp;</td>
		  </tr>
		  
		</table>
	</td>
	</tr><tr> 
        <td align="center" colspan="2"> 
		<goa:button property="save" value="保存" styleClass="button"  onclick="return doSave(NationForm)" operation="MZWF.OPT"/>
		<goa:button property="save" value="保存并返回" styleClass="button"  onclick="return doSaveExit(NationForm)" operation="MZWF.OPT"/>
          <goa:button property="exit" value="返回" styleClass="button" onclick="doAction('goUrl(index)')" operation="MZWF.VIEW"/></td>
      </tr></table>
    </center>
</div>

</html:form>
</body>

