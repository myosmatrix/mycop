package com.gever.goa.user.action;

import org.apache.struts.action.*;
import com.gever.goa.base.*;
import com.gever.vo.*;
import com.gever.exception.DefaultException;
import javax.servlet.http.HttpServletRequest;
import com.gever.util.StringUtils;

import com.gever.goa.user.dao.*;
import com.gever.goa.user.dao.impl.*;
import com.gever.goa.user.vo.UserVO;
import com.gever.goa.user.dao.UserFactory;

/**
 * Comment block is generated by javaClassComment.vsl
 *
 * <p>Title: </p>
 * <p>Description: TUser ʵ��</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company:${config.company}</p>
 * @Author:		${config.author}
 * @Version:${config.version}
 *
 */
public class UserAction extends BaseAction {
    UserDao myDao = null;
    public UserAction() {}

    /**
     * ��ʼ��ҳ������
     * @param actionform ��ǰ��vo����
     * @return ��ǰ���õ�vo����
     * @throws DefaultException
     */
    protected void initData(GoaActionConfig cfg) throws DefaultException,
        Exception {

        UserForm myForm = (UserForm) cfg.getBaseForm(); //�õ�form����
        myDao = UserFactory.getInstance().createUserDAO(
        super.dbData); //�õ����Ӧdao��ʵ��
        cfg.setBaseDao( (BaseDAO) myDao);

        if (myForm.getVo() == null){
            myForm.setVo(new UserVO());
        }
    }

    /**
     * ���嵥ҳ��
     * @param cfg ��ǰAction���������
     * @return forward��ַ
     * @throws DefaultException
     * @throws Exception
     */

    protected String toList(GoaActionConfig cfg) throws DefaultException,
        Exception {
       HttpServletRequest request =  cfg.getRequest();
       String nodeid = StringUtils.nullToString(request.getParameter("nodeid"));
       if (!"".equals(nodeid)) {		                 //��xmlTree�������ʱ
           cfg.getBaseForm().setSqlWhere(" and 1 = '" + nodeid + "'");
       } else {
           cfg.getBaseForm().setSqlWhere("");    //û��xmlTree�������ʱ
       }
       super.toList(cfg);
       log.showLog("------getPageControl---"+  cfg.getBaseForm().getPageControl().getData().size());
        return this.FORWORD_LIST_PAGE;
    }

    /**
     * ��ȡtree������
     * @param cfg ��ǰAction���������
     * @return forward��ַ
     * @throws DefaultException
     * @throws Exception
     */
    public String doTreeData(GoaActionConfig cfg) throws DefaultException,
        Exception{
        String nodeid = StringUtils.nullToString(cfg.getRequest().getParameter("nodeid"));

	        //��ȡnodeid���ӽڵ�����
        cfg.getRequest().setAttribute("treeData", myDao.getTreeData(nodeid));
        return TREE_PAGE;
    }
}