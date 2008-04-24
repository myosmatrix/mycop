package com.gever.goa.dailyoffice.targetmng.action;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.gever.exception.DefaultException;
import com.gever.goa.dailyoffice.message.dao.MessageDao;
import com.gever.goa.dailyoffice.message.dao.MessageFactory;
import com.gever.goa.dailyoffice.targetmng.dao.YearTargetDao;
import com.gever.goa.dailyoffice.targetmng.dao.YearTargetFactory;
import com.gever.goa.dailyoffice.targetmng.vo.TargetVO;
import com.gever.goa.infoservice.dao.IsCustomerFactory;
import com.gever.goa.infoservice.dao.IsStandardModelDao;
import com.gever.goa.infoservice.vo.IsStandardModelVO;
import com.gever.goa.util.ConstantSet;
import com.gever.goa.web.util.TemplateUtils;
import com.gever.jdbc.BaseDAO;
import com.gever.struts.Constant;
import com.gever.struts.action.BaseAction;
import com.gever.struts.action.GoaActionConfig;
import com.gever.sysman.api.OrganizationUtil;
import com.gever.sysman.organization.dao.OrganizationFactory;
import com.gever.sysman.organization.dao.UserDAO;
import com.gever.sysman.organization.model.User;
import com.gever.sysman.organization.model.impl.DefaultUser;
import com.gever.util.Codes;
import com.gever.util.DateTimeUtils;
import com.gever.util.StringUtils;
import com.gever.util.SumUtils;

/**
 * Title: ���Ŀ�������
 * Description: ���Ŀ�������
 * Copyright: Copyright (c) 2004
 * Company: ��������
 * @author Hu.Walker
 * @version 1.0
 */

public class YearTargetAction
    extends BaseAction {
  YearTargetDao yeartargetDao = null; //����ӿ�
  OrganizationUtil util = new OrganizationUtil();
  public YearTargetAction() {
  }

  /**
   * ��ʼ��ҳ������
   * @param cfg ��ǰ��vo����
   * @throws DefaultException
   * @throws Exception
   */
  protected void initData(GoaActionConfig cfg) throws DefaultException,
      Exception {
    YearTargetForm myForm = (YearTargetForm) cfg.getBaseForm(); //�õ�form����
    yeartargetDao = YearTargetFactory.getInstance().createYearTarget(super.
        dbData); //�õ����Ӧdao��ʵ��
    cfg.setBaseDao( (BaseDAO) yeartargetDao); //cfg--Goa Action��ȡ��--���ó����е�Dao
    //�ݴ�����,��ֹvo����Ϊnull
    if (myForm.getVo() == null) {
      myForm.setVo(new TargetVO()); //����VO��Form��
    }
  }

  /**
   * ���嵥ҳ��--����������BaseAction�е�toList����
   * �б�����--��ȡ��Щ��ǰ���(������ǲ�ѯ��������)
   * ����Ϊ4-���Ŀ�����Щ���Ŀ��
   * @param cfg ��ǰAction���������
   * @return forward��ַ
   * @throws DefaultException
   * @throws Exception
   */

  protected String toList(GoaActionConfig cfg) throws DefaultException,
      Exception {
    HttpServletRequest request = cfg.getRequest();
    HttpSession session=cfg.getSession();
    String curUserID = cfg.getBaseForm().getUserId(); //ȡ�õ�ǰ�û�ID
    String user_dev=(String) session.getAttribute(Constant.USER_FILTER);//�õ��û�����
    String curDeptCodes = cfg.getBaseForm().getCurDeptCodes().trim();
     //�õ��Ӳ���
    StringTokenizer stDept = new StringTokenizer(curDeptCodes);
    OrganizationUtil systemMngUtil=new OrganizationUtil();
    String curSonDept="";
    while (stDept.hasMoreTokens()) {
        String curDeptId = stDept.nextToken();
        System.out.println("The curDeptId is:--------" + curDeptId);
        curSonDept = curSonDept + systemMngUtil.getDepartmentSonIdById(curDeptId);
    }
    ConstantSet.CurUserID = curUserID;
    String queryFlag = request.getParameter("queryFlag");
    String curYear = DateTimeUtils.getCurrentDate().substring(0, 4); //��ȡ��ǰ���
    StringBuffer sbSqlWhere = new StringBuffer();
    if ("true".equals(queryFlag)) {
      String searchYear = ( (YearTargetForm) cfg.getBaseForm()).getSearchYear();
      sbSqlWhere.append(" and CURRENT_YEAR like '%" +searchYear + "%' ");
      sbSqlWhere.append(" and TARGET_TYPE='" + ConstantSet.YearTargetType +"' ");
      sbSqlWhere.append(" and ").append(super.getIsNull("CURRENT_MONTH"));
      sbSqlWhere.append(" and ").append(super.getIsNull("CURRENT_WORK"));
      sbSqlWhere.append(" and ( ").append(user_dev).append("<b.level");
      sbSqlWhere.append(" or user_code= ").append(curUserID).append(")");
      if(!StringUtils.isNull(user_dev)&&!StringUtils.isNull(curDeptCodes)){
          if (Integer.parseInt(user_dev) > 3) {
              StringTokenizer st = new StringTokenizer(curSonDept, ",");
              int stCount = st.countTokens();
              sbSqlWhere.append("and (");
              while (st.hasMoreTokens()) {
                  sbSqlWhere.append(" dept_code like'%").append(st.nextToken());
                  sbSqlWhere.append(",%'");
                  if (st.hasMoreTokens()) {
                      sbSqlWhere.append(" or ");
                  }
              }
              //���û���Ӳ���
              if (stCount > 0) {
                  sbSqlWhere.append(" or ");
              }
              sbSqlWhere.append(" dept_code like '%").append(curDeptCodes).append(",%')");
          }
      }
    } else {
      sbSqlWhere.append(" and CURRENT_YEAR = '" + curYear +"' ");
      sbSqlWhere.append(" and TARGET_TYPE='" + ConstantSet.YearTargetType +"' ");
      sbSqlWhere.append(" and ").append(super.getIsNull("CURRENT_MONTH"));
      sbSqlWhere.append(" and ").append(super.getIsNull("CURRENT_WORK"));
      sbSqlWhere.append(" and ( ").append(user_dev).append("<b.level");
      sbSqlWhere.append(" or user_code= ").append(curUserID).append(")");
      if(!StringUtils.isNull(user_dev)&&!StringUtils.isNull(curDeptCodes)){
          if (Integer.parseInt(user_dev) > 3) {
              StringTokenizer st = new StringTokenizer(curSonDept, ",");
              int stCount = st.countTokens();
              sbSqlWhere.append("and (");
              while (st.hasMoreTokens()) {
                  sbSqlWhere.append(" dept_code like'%").append(st.nextToken());
                  sbSqlWhere.append(",%'");
                  if (st.hasMoreTokens()) {
                      sbSqlWhere.append(" or ");
                  }
              }
              //���û���Ӳ���
              if (stCount > 0) {
                  sbSqlWhere.append(" or ");
              }
              sbSqlWhere.append(" dept_code like '%").append(curDeptCodes).append(",%')");
          }
      }
    }
    if (!StringUtils.isNull(curYear)) {
      cfg.getBaseForm().setSqlWhere(sbSqlWhere.toString());
    }
    super.toList(cfg);
    return this.FORWORD_LIST_PAGE;
  }

  /**
   * ���޸�ҳ��--����������BaseAction�е�toEdit����
   * �Ա��ʼ������ҳ���ϵ�һЩ�������ݣ��������룬ֻ���ȡ��
   * @param cfg ��ǰAction���������
   * @return forward��ַ
   * @throws DefaultException
   * @throws Exception
   */
  protected String toEdit(GoaActionConfig cfg) throws DefaultException,
      Exception {
    super.toEdit(cfg);
    String actionType = SumUtils.nullToString(cfg.getRequest().getParameter(
        "actionType"));
    YearTargetForm myForm = (YearTargetForm) cfg.getBaseForm(); //�õ�form����
    if (StringUtils.isNull(myForm.getReportTemplateName())) { //�������ģ�廹û������
      IsStandardModelDao isStandardModelDao = IsCustomerFactory.
          getInstance().createIsStandardModel(super.dbData);
      IsStandardModelVO ismVO = (IsStandardModelVO) isStandardModelDao.
          getTemplate(ConstantSet.YearTargetTemplate);
      myForm.setReportTemplateName(ismVO.getFile_path());
    }
    User user = new DefaultUser();
    UserDAO userDao = OrganizationFactory.getInstance().getUserDAO();
    OrganizationUtil systemMngUtil = new OrganizationUtil();
    String curUser = cfg.getBaseForm().getUserId(); //��ǰ�û�ID
    String deptCodesOfCurUser = systemMngUtil.getDepartmentIDsByUser(
        Integer.parseInt(curUser));
    //��ȡ��ǰ�û���Ӧ�Ĳ��Ŵ��룬�ö��Ÿ���
    //String deptNamesOfCurUser = systemMngUtil.getDepartmentNamesByUser(Integer.parseInt(curUser));
    //��ȡ��ǰ�û���Ӧ�Ĳ������ƣ��ö��Ÿ���
    String deptNamesOfCurUser = cfg.getBaseForm().getCurDeptNames();
    //��ȡ��ǰ�û���Ӧ�Ĳ������ƣ��ÿո����
    int i = 0;
    StringTokenizer st = new StringTokenizer(deptCodesOfCurUser, ",");
    if (st.hasMoreTokens()) {
      i++;
      String curDeptCode = st.nextToken();
    }
    String curDate = DateTimeUtils.getCurrentDate(); //ȡ�õ�ǰ����--��Ӧ��������
    TargetVO vo = (TargetVO) cfg.getBaseForm().getVo();
    String curYear = "";
    String curMonth = "";
    String curWeek = "";
    if (!this.MODIFY_ACTION.equals(actionType)) { //����ǵ�����ҳ��-��Ҫ����������
      curYear = DateTimeUtils.getCurrentDate().substring(0, 4); //��ȡ��ǰ���
      vo.setCurrent_year(curYear);
      vo.setCurrent_month(curMonth); //�¿�
      vo.setCurrent_work(curWeek); //�ܿ�
    } else { //����ǵ��޸�ҳ����ǰ����Ҫ�����ݿ��л�ȡ
      System.out.println("modify");
      //���Բ���ʲô��������Ϊ���������ݿ����Զ���ȡvo
    }
    String initUserCode = vo.getUser_code();
    if ("".equals(initUserCode) || initUserCode == null) {
      //�����ʼ�û�Ϊ��(���������)�����õ�ǰ�û�Ϊ������
      vo.setUser_code(curUser);
    }

    //���źʹ�������Ϊ����ʱ�Ĳ��ź�����
   if(!this.MODIFY_ACTION.equals(actionType)){
       vo.setCreate_date(curDate);
       vo.setDept_code(deptCodesOfCurUser);
       myForm.setDeptname(deptCodesOfCurUser);
   }

    vo.setTarget_type(ConstantSet.YearTargetType);
    String auditorid = vo.getAuditor();
    String checkerid = vo.getChecker();
    if (!"".equals(auditorid) && auditorid != null) {
      int iAuditorid = Integer.parseInt(auditorid);
      if (util.checkUser(iAuditorid)) {
        User userAuditor = new DefaultUser();
        userAuditor = userDao.findUserByID(iAuditorid);
        String auditorName = userAuditor.getName();
        myForm.setAuditorid(auditorid); //��������һ��FormBean�е������ID���������ݿ��н�д���ֵ
        myForm.setAuditorname(auditorName); //�������ݿ��л�ȡ���������ʾ��ҳ��
      } else {
        myForm.setAuditorname("");
      }
    } else {
      myForm.setAuditorid(""); //������������������ϴε�FormBean�е�ֵ����Ϊ��
      myForm.setAuditorname("");
    }

    if (!"".equals(checkerid) && checkerid != null) {
      int iCheckerid = Integer.parseInt(checkerid);
      if (util.checkUser(iCheckerid)) {
        User userChecker = new DefaultUser();
        userChecker = userDao.findUserByID(iCheckerid);
        String checkerName = userChecker.getName();
        myForm.setCheckerid(checkerid); //��������һ��FormBean�е�������ID���������ݿ��н�д���ֵ
        myForm.setCheckername(checkerName); //�������ݿ��л�ȡ����������ʾ��ҳ��
      } else {
        myForm.setCheckername("");
      }
    } else {
      myForm.setCheckerid(""); //������������������ϴε�FormBean�е�ֵ����Ϊ��
      myForm.setCheckername("");
    }

    //�õ����û����Ӧdao��ʵ��
    user = userDao.findUserByID(Integer.parseInt(vo.getUser_code()));
    String curName = user.getName();
    myForm.setUsername(curName); //���õ�ǰ�û��������������˻�--��Ӧ������
    String auditor = vo.getAuditor(); //��ȡ�����
    String checker = vo.getChecker(); //��ȡ������
    String user_code = vo.getUser_code(); //��ȡ������
    String audit_flag = vo.getAudit_flag(); //��ȡ���״̬
    String check_flag = vo.getCheck_flag(); //��ȡ����״̬
    int auditFlag = 0;
    int checkFlag = 0;
    int editFlag = 0;
    int isReadOnlyTemplateFlag = 0;
    auditFlag = yeartargetDao.judgeAuditTag(curUser, auditor, audit_flag,
                                            check_flag);
    checkFlag = yeartargetDao.judgeCheckTag(curUser, checker, audit_flag,
                                            check_flag);
    isReadOnlyTemplateFlag = ConstantSet.judgeReadOnlyTemplateTag(curUser,
        user_code, check_flag, checker,
        audit_flag, auditor);
    //�����ж��Ƿ���ʾ��˻�������Ϣ
    String flag = SumUtils.nullToString(cfg.getRequest().getParameter(
        "flag"));
    System.out.println("The flag is:" + flag);
    if (flag.equals("modify")) {
      myForm.setAuditFlag("0");
      myForm.setCheckFlag("0");
    }
    if (!"".equals(audit_flag) && audit_flag != null) { //�Ѿ����������ʱ
      //ֻ����˱�ǲ�Ϊnull���ж�(˵�����������ļ�¼)������Ĭ��Ϊ����ѡ������˺�������
      editFlag = ConstantSet.judgeEditTag(curUser, user_code, audit_flag);
    } else { //��û�о�����˵�����£�һ���ǿ����޸ĵ�
      editFlag = 1;
    }
    if (!flag.equals("modify")) { //����û�����޸İ�ť��������޸���ˣ�������
      editFlag = 0;
    }
    if (auditFlag == 1 && !flag.equals("modify")) { //��ǰ���������ʱ
      myForm.setAuditFlag("1"); //ӵ�����Ȩ��--��ʾ��������д�������Ƿ�ͨ��ordio
      vo.setAudit_date(curDate); //�������������Ϊ��ǰ������2004-07-28
    } else if (auditFlag == 0) {
      myForm.setAuditFlag("0"); //��ӵ�����Ȩ��--����ʾ��������д�������Ƿ�ͨ��ordio
    }
    if (checkFlag == 1 && !flag.equals("modify")) { //��ǰ����������ʱ
      myForm.setCheckFlag("1"); //ӵ������Ȩ��--��ʾ���������д��������Ƿ�ͨ��ordio
      vo.setCheck_date(curDate); //��������������Ϊ��ǰ������2004-07-28
    } else if (checkFlag == 0) {
      myForm.setCheckFlag("0"); //��ӵ������Ȩ��--����ʾ���������д��������Ƿ�ͨ��ordio
    }
    if (isReadOnlyTemplateFlag == 0) { //��ǰ���ǲ������޸�Ŀ��ģ��ʱ-ֻ��
      myForm.setIsReadOnlyTemplateFlag("0"); //��ӵ���޸�Ȩ��-��ģ��ֻ����־��Ϊֻ��
    } else if (isReadOnlyTemplateFlag == 1) { //��д
      myForm.setIsReadOnlyTemplateFlag("1"); //ӵ���޸�Ȩ��-����ģ��ֻ����־��Ϊ��ֻ��
    }
    if (editFlag == 1) { //��ǰ���ǿ��޸���ʱ
      myForm.setEditFlag("1"); //ӵ���޸�Ȩ��--��ʾ��˺�������ѡ��ť
    } else if (editFlag == 0) {
      myForm.setEditFlag("0"); //��ӵ���޸�Ȩ��--����ʾ��˺�������ѡ��ť
    }

    //��Ϊ�ڴ���ȡ����˺�������ʱ�򣬱�־λisCancel���г�ͻ��������toEdit����
    //����Կ���,����ֵΪ0
    if (actionType.equals("modify")) {
      myForm.setIsCancel("0");
    }
    cfg.getBaseForm().setVo(vo);
    ConstantSet cs = new ConstantSet();
    myForm.setUnitname(cs.getUnitName());
    if ("modify".equals(actionType)) { //������޸Ĳ���
      makeCellField(cfg);
    }
    if (!this.MODIFY_ACTION.equals(actionType)) { //��������޸���ת�Ļ��ܵ�����ҳ��ȥ
      myForm.setEditFlag("1"); //������ܵ�����ҳ��ȥ�Ļ���Ĭ�Ͽ����޸�
      cfg.getBaseForm().setActionType(super.ADD_ACTION);
    }
    return this.FORWORD_EDIT_PAGE;
  }

  /**
   * ��������--��������BaseAction����ķ�����ʵ������ģ��Ĺ���
   * @param cfg ��ǰAction���������
   * @param isBack �Ƿ񷵻�
   * @return forward��ַ
   * @throws DefaultException
   * @throws Exception
   */

  protected String doInsert(GoaActionConfig cfg, boolean isBack) throws
      DefaultException, Exception {
    YearTargetForm yeartargetForm = (YearTargetForm) cfg.getBaseForm();
    TargetVO vo = (TargetVO) yeartargetForm.getVo();
    vo.setTarget_content(Codes.decode(yeartargetForm.getCellcontent().
                                      toCharArray()));

    //�����ж��û���д�������Ƿ��Ѿ����������ݿ�����
    String current_year = vo.getCurrent_year();
    String current_month = vo.getCurrent_month();
    String current_week = vo.getCurrent_work();
    String target_type = vo.getTarget_type();
    String dept_code = vo.getDept_code();
    String user_code = vo.getUser_code();
    ConstantSet constantSet = new ConstantSet();
    String isExistFlag = constantSet.queryTargetIsExist(current_year,
        current_month, current_week, target_type,
        dept_code, user_code);
    if ("1".equals(isExistFlag)) {
      throw new DefaultException("���Ѿ���д�˸�������������ˣ���ѡ��༭���");
    }
    String auditorid = yeartargetForm.getAuditorid(); //��ȡ�����id
    String auditorname = yeartargetForm.getAuditorname(); //��ȡ�����������
    String checkerid = yeartargetForm.getCheckerid(); //��ȡ������id
    String checkername = yeartargetForm.getCheckername(); //��ȡ������������
    vo.setAuditor(auditorid);
    vo.setChecker(checkerid);
    vo.setAudit_flag("0");
    vo.setCheck_flag("0"); //Ĭ�Ͻ����������־��Ϊ0
    //vo.setAuditor(auditorname);//��������������-��ʱͨ���û���(username�ֶ�)���ж�Ȩ��
    //vo.setChecker(checkername);
    cfg.getBaseForm().setVo(vo);
    String retStr = "";
    try {
      cfg.getBaseDao().insert(vo);
    } catch (DefaultException e) {
      throw new DefaultException("���Ѿ���д�˸�������������ˣ���ѡ��༭���");
    }
    //����������˷��Ͷ���Ϣ
    String isSendMsg = yeartargetForm.getIsSendMsg(); //�Ƿ��Ͷ���Ϣ
    System.out.println("The isSendMsg is:----" + isSendMsg);
    if (!StringUtils.isNull(isSendMsg)) {
      if ("true".equalsIgnoreCase(isSendMsg)) {
        this.sendMsg(vo);
      }
    }

    if (isBack == true) {
      retStr = this.FORWORD_LIST_PAGE;
    } else {
      cfg.getBaseForm().setActionType(this.MODIFY_ACTION);
      this.makeCellField(cfg);
      retStr = this.FORWORD_EDIT_PAGE;
    }
    return retStr;
  }

  /**
   * ���Ͷ���Ϣ
   * @return voie
   */
  private void sendMsg(TargetVO vo) throws Exception {
    MessageDao messageDao = MessageFactory.getInstance().
        createMessageDao(Constant.DATA_SOURCE);
    String userCode = vo.getUser_code(); //�������Ǵ�����
    String sendTime = DateTimeUtils.getCurrentDateTime();
    String auditorContent = "����!Ŀ�������������Ҫ��˵���Ŀ��!";
    String checkerContent = "����!Ŀ�������������Ҫ��������Ŀ��!";
    String receiver1 = vo.getAuditor();
    String receiver2 = vo.getChecker();
    String webUrl = "/dailyoffice/targetmng/viewyeartarget.do?fid="+vo.getSerial_num();
    System.out.println("the weburl is--"+webUrl);
    messageDao.sendMessage(userCode, sendTime, auditorContent, receiver1, webUrl,"ת����Ŀ�����");
    messageDao.sendMessage(userCode, sendTime, checkerContent, receiver2, webUrl,"ת����Ŀ������");
  }

  /**
   * �޸Ķ���--��������BaseAction����ķ�����ʵ���޸�ģ��Ĺ���
   * @param cfg ��ǰAction���������
   * @param isBack �Ƿ񷵻�
   * @return forward��ַ
   * @throws DefaultException
   * @throws Exception
   */
  protected String doUpdate(GoaActionConfig cfg, boolean isBack) throws
      DefaultException,
      Exception {
    YearTargetForm yeartargetForm = (YearTargetForm) cfg.getBaseForm();
    TargetVO vo = (TargetVO) yeartargetForm.getVo();
    vo.setTarget_content(Codes.decode(yeartargetForm.getCellcontent().
                                      toCharArray()));

    String isCancel = yeartargetForm.getIsCancel();
    System.out.println("The isCancel is:***" + isCancel);
    //�ж��û��Ƿ�ȡ����˻�����
    if ("1".equals(isCancel)) {
      vo.setAudit_flag("0");
      vo.setAudit_date("");
      vo.setAudit_opinion("");
    }
    if ("2".equals(isCancel)) {
      vo.setCheck_flag("0");
      vo.setCheck_date("");
      vo.setCheck_opinion("");
    }
    if (! ("1".equals(isCancel) || "2".equals(isCancel))) {
      String auditorid = yeartargetForm.getAuditorid(); //��ȡ�����
      String checkerid = yeartargetForm.getCheckerid(); //��ȡ������
      vo.setAuditor(auditorid);
      vo.setChecker(checkerid);
    }

    super.doUpdate(cfg, isBack);
    //����������˷��Ͷ���Ϣ
    String isSendMsg = yeartargetForm.getIsSendMsg(); //�Ƿ��Ͷ���Ϣ
    System.out.println("The isSendMsg is:----" + isSendMsg);
    if (!StringUtils.isNull(isSendMsg)) {
      if ("true".equalsIgnoreCase(isSendMsg)) {
        this.sendMsg(vo);
      }
    }

    String retStr = "";
    if (isBack == true) {
      retStr = this.FORWORD_LIST_PAGE;
    } else {
      this.makeCellField(cfg);
      retStr = this.FORWORD_EDIT_PAGE;
    }
    return retStr;

  }

  /**
   * ������ҳ��
   * @param cfg ��ǰAction���������
   * @return forward ��ַ
   * @throws DefaultException
   * @throws Exception
   */

  protected String toView(GoaActionConfig cfg) throws DefaultException,
      Exception {
    String forwardpath = super.toView(cfg);
    YearTargetForm myForm = (YearTargetForm) cfg.getBaseForm(); //�õ�form����
    TargetVO vo = (TargetVO) cfg.getBaseForm().getVo();
    UserDAO userDao = OrganizationFactory.getInstance().getUserDAO();
    String usercode = vo.getUser_code(); //��ȡ������ID
    String deptcode = vo.getDept_code(); //��ȡ����ID-���ﲻ��ͨ��deptcode����ȡdeptname��
    String auditorid = vo.getAuditor(); //��ȡ�����ID
    String checkerid = vo.getChecker(); //��ȡ������ID
    if (!"".equals(usercode) && usercode != null) {
      int iUsercode = Integer.parseInt(usercode);
      User user = new DefaultUser();
      user = userDao.findUserByID(iUsercode);
      String userName = user.getName();
      myForm.setUsername(userName); //���ô�����������
      OrganizationUtil systemMngUtil = new OrganizationUtil();
      String curDeptName = systemMngUtil.getDepartmentNamesByUser(
          iUsercode);
      myForm.setDeptname(curDeptName); //���ô��������ڲ���������
    }
    /*
             if (!"".equals(deptcode) && deptcode != null) {
        int iDeptcode = Integer.parseInt(deptcode);
        Department dept = new DefaultDepartment();
         DepartmentDAO deptDao = OrganizationFactory.getInstance().getDepartmentDAO();
        dept = deptDao.findDepartmentByID(iDeptcode);
        String curDeptName = dept.getName();
        myForm.setDeptname(curDeptName); //���õ�ǰ����������--��Ӧ��ǰ�������Ĳ���
             }
     */
    if (!"".equals(auditorid) && auditorid != null) {
      int iAuditorid = Integer.parseInt(auditorid);
      if (util.checkUser(iAuditorid)) {
        User userAuditor = new DefaultUser();
        userAuditor = userDao.findUserByID(iAuditorid);
        String auditorName = userAuditor.getName();
        myForm.setAuditorname(auditorName);
        //�������ݿ��л�ȡ���������ʾ��ҳ��
      } else {
        myForm.setAuditorname("");
      }
    }else{
      myForm.setAuditorname("");
    }
    if (!"".equals(checkerid) && checkerid != null) {
      int iCheckerid = Integer.parseInt(checkerid);
      if (util.checkUser(iCheckerid)) {
        User userChecker = new DefaultUser();
        userChecker = userDao.findUserByID(iCheckerid);
        String checkerName = userChecker.getName();
        myForm.setCheckername(checkerName); //�������ݿ��л�ȡ����������ʾ��ҳ��
      } else {
        myForm.setCheckername("");
      }
    }else{
      myForm.setCheckername("");
    }

    int creatorFlag = 0;
    String curUser = cfg.getBaseForm().getUserId(); //ȡ�õ�ǰ�û�ID
    //creatorFlag = ConstantSet.judgeCreatorTag(curUser, usercode);
    String auditflag = vo.getAudit_flag(); //��ȡ��˱�־
    String checkflag = vo.getCheck_flag(); //��ȡ������־
    //���ﲻ����Ҫͨ���Ƿ�Ϊ���������жϵģ�����Ҫ���ǵ���ǰ��¼�Ƿ񾭹���˺�����
    //creatorFlag = ConstantSet.judgeEditTagForView(curUser, usercode, checkflag, checkerid, auditflag, auditorid);
    creatorFlag = ConstantSet.judgeEditTagForViewOfNew(curUser, usercode,
        checkflag, checkerid, auditflag,
        auditorid);
    if (creatorFlag == 1) {
      myForm.setCreatorFlag("1");
    } else if (creatorFlag == 2) {
      myForm.setCreatorFlag("2"); //ӵ�����Ȩ��
    } else if (creatorFlag == 3) {
      myForm.setCreatorFlag("3"); //ӵ������Ȩ��
    } else if (creatorFlag == 0) {
      myForm.setCreatorFlag("0"); //��ӵ���޸�Ȩ��
    }
    int modify_flag = ConstantSet.judgeModify(curUser, usercode, auditorid,
                                              checkerid, auditflag,
                                              checkflag);
    if (modify_flag == 1) {
      myForm.setModifyFlag("1");
    } else if (modify_flag == 0) {
      myForm.setModifyFlag("0");
    }
    System.out.println("The modify_falg is***:" + modify_flag);
    //�ж��Ƿ���ʾȡ����˺�ȡ������
    int isCancel = ConstantSet.judgeIsCancel(curUser, auditorid, checkerid,
                                             auditflag, checkflag);
    if (isCancel == 1) {
      myForm.setIsCancel("1");
    } else if (isCancel == 2) {
      myForm.setIsCancel("2");
    } else if (isCancel == 0) {
      myForm.setIsCancel("0");
    }
    ConstantSet cs = new ConstantSet();
    myForm.setUnitname(cs.getUnitName());
    makeCellField(cfg);
    return forwardpath;
  }

//������ʱ�ļ��Ա��ѯ
  private void makeCellField(GoaActionConfig cfg) throws DefaultException,
      Exception {
    TemplateUtils templateUtils = new TemplateUtils();
    YearTargetForm yeartargetForm = (YearTargetForm) cfg.getBaseForm();
    TargetVO vo = (TargetVO) yeartargetForm.getVo();
    String strPath = super.getServlet().getServletContext().getRealPath("/");
    String strFilename = "/cell/report/" +
        templateUtils.makeCellName(strPath + "/cell/report") + ".cll";
    yeartargetForm.setCellname(strFilename);
    byte[] bcell = vo.getTarget_content();
    File file = new File(strPath + strFilename);
    OutputStream fos = new FileOutputStream(file);
    OutputStream os = new BufferedOutputStream(fos);
    os.write(bcell);
    os.close();
  }

}