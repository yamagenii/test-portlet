/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2011 Aimluck,Inc.
 * http://www.aipo.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.aimluck.eip.test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.jar.Attributes;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.jetspeed.services.rundata.JetspeedRunData;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALStringField;
import com.aimluck.commons.utils.ALDateUtil;
import com.aimluck.eip.cayenne.om.portlet.EipTTest;
import com.aimluck.eip.cayenne.om.security.TurbineGroup;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.cayenne.om.security.TurbineUserGroupRole;
import com.aimluck.eip.common.ALAbstractMultiFilterSelectData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALEipGroup;
import com.aimluck.eip.common.ALEipManager;
import com.aimluck.eip.common.ALEipPost;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.ResultList;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.accessctl.ALAccessControlFactoryService;
import com.aimluck.eip.services.accessctl.ALAccessControlHandler;
import com.aimluck.eip.test.util.TestUtils;
import com.aimluck.eip.util.ALCommonUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * Test検索データを管理するクラスです。 <BR>
 *
 */
public class TestSelectData extends
    ALAbstractMultiFilterSelectData<EipTTest, EipTTest> implements ALData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(TestSelectData.class.getName());

  /** 現在選択されているタブ */
  private String currentTab;

  /** カテゴリ一覧 */
//  private List<TestCategoryResultData> categoryList;

  /** 部署一覧 */
  private List<ALEipGroup> postList;

  /** Test の総数 */
  private int testSum;

  /** ポートレット Schedule への URL */
  private String scheduleUrl;

  private String target_group_name;

  private String target_user_id;

  private List<ALEipGroup> myGroupList;

  private int login_user_id;

  private boolean hasAclEditTestOther;

  private boolean hasAclDeleteTestOther;

  private int table_colum_num;

  /** カテゴリの初期値を取得する */
  private String filterType = "";

  /** カテゴリ　ID */
//  private String categoryId = "";

  /** ターゲット　 */
  private ALStringField target_keyword;

  /**
   *
   * @param action
   * @param rundata
   * @param context
   * @throws ALPageNotFoundException
   * @throws ALDBErrorException
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {

    String sort = ALEipUtils.getTemp(rundata, context, LIST_SORT_STR);
    if (sort == null || sort.equals("")) {
      ALEipUtils.setTemp(
        rundata,
        context,
        LIST_SORT_STR,
        EipTTest.UPDATE_DATE_PROPERTY);
      ALEipUtils.setTemp(
        rundata,
        context,
        LIST_SORT_TYPE_STR,
        ALEipConstants.LIST_SORT_TYPE_DESC);
    }

    String tabParam = rundata.getParameters().getString("tab");
    currentTab = ALEipUtils.getTemp(rundata, context, "tab");
    if (tabParam == null && currentTab == null) {
      ALEipUtils.setTemp(rundata, context, "tab", "list");
      currentTab = "list";
    } else if (tabParam != null) {
      ALEipUtils.setTemp(rundata, context, "tab", tabParam);
      currentTab = tabParam;
    }

    login_user_id = ALEipUtils.getUserId(rundata);

    ALAccessControlFactoryService aclservice =
      (ALAccessControlFactoryService) ((TurbineServices) TurbineServices
        .getInstance()).getService(ALAccessControlFactoryService.SERVICE_NAME);
    ALAccessControlHandler aclhandler = aclservice.getAccessControlHandler();

    // アクセス権(他人のTest編集)
    hasAclEditTestOther =
      aclhandler.hasAuthority(
        login_user_id,
        ALAccessControlConstants.POERTLET_FEATURE_TODO_TODO_OTHER,
        ALAccessControlConstants.VALUE_ACL_UPDATE);

    // アクセス権(他人のTest削除)
    hasAclDeleteTestOther =
      aclhandler.hasAuthority(
        login_user_id,
        ALAccessControlConstants.POERTLET_FEATURE_TODO_TODO_OTHER,
        ALAccessControlConstants.VALUE_ACL_DELETE);

    // My グループの一覧を取得する．
    postList = ALEipUtils.getMyGroups(rundata);

    // カテゴリの初期値を取得する
//    try {
//      filterType = rundata.getParameters().getString("filtertype", "");
//      if (filterType.equals("category")) {
//        String categoryId = rundata.getParameters().getString("filter", "");
//        if (!categoryId.equals("")) {
//          this.categoryId = categoryId;
//        } else {
//          VelocityPortlet portlet = ALEipUtils.getPortlet(rundata, context);
//          this.categoryId =
//            portlet.getPortletConfig().getInitParameter("p3a-category");
//        }
//      }
//    } catch (Exception ex) {
//      logger.error("test", ex);
//    }

    target_keyword = new ALStringField();
    super.init(action, rundata, context);

  }

  /**
   *
   * @param rundata
   * @param context
   */
//  public void loadCategoryList(RunData rundata) {
//    categoryList = TestUtils.getCategoryList(rundata);
//  }

  /**
   * 一覧データを取得します。 <BR>
   *
   * @param rundata
   * @param context
   * @return
   * @throws ALDBErrorException
   */
  @Override
  public ResultList<EipTTest> selectList(RunData rundata, Context context) {
    try {
      if (TestUtils.hasResetFlag(rundata, context)) {
        TestUtils.resetFilter(rundata, context, this.getClass().getName());
        target_keyword.setValue("");
      }
      if (TestUtils.hasResetKeywordFlag(rundata, context)) {
        TestUtils.resetKeyword(rundata, context, this.getClass().getName());
      }
      if (TestUtils.hasResetTargetFlag(rundata, context)) {
        TestUtils.resetTarget(rundata, context, this.getClass().getName());
      }
      target_group_name = TestUtils.getTargetGroupName(rundata, context);
      target_user_id = TestUtils.getTargetUserId(rundata, context);
      target_keyword.setValue(TestUtils.getTargetKeyword(rundata, context));

      setMyGroupList(new ArrayList<ALEipGroup>());
      getMyGroupList().addAll(ALEipUtils.getMyGroups(rundata));

      SelectQuery<EipTTest> query = getSelectQuery(rundata, context);
      buildSelectQueryForListView(query);
      buildSelectQueryForListViewSort(query, rundata, context);

      ResultList<EipTTest> list = query.getResultList();
      setPageParam(list.getTotalCount());
      testSum = list.getTotalCount();
      return list;
    } catch (Exception ex) {
      logger.error("test", ex);
      return null;
    }
  }

  /**
   * 検索条件を設定した SelectQuery を返します。 <BR>
   *
   * @param rundata
   * @param context
   * @return
   */
  private SelectQuery<EipTTest> getSelectQuery(RunData rundata, Context context) {
    SelectQuery<EipTTest> query = Database.query(EipTTest.class);
    Expression exp1;
    if ((target_user_id != null)
      && (!target_user_id.equals(""))
      && (!target_user_id.equals("all"))) {
      exp1 =
        ExpressionFactory.matchDbExp(TurbineUser.USER_ID_PK_COLUMN, Integer
          .valueOf(target_user_id));
      // exp0.andExp(exp1);
      query.andQualifier(exp1);
    }

    if ((target_group_name != null)
      && (!target_group_name.equals(""))
      && (!target_group_name.equals("all"))) {
      // 選択したグループを指定する．
      Expression exp =
        ExpressionFactory.matchExp(EipTTest.TURBINE_USER_PROPERTY
          + "."
          + TurbineUser.TURBINE_USER_GROUP_ROLE_PROPERTY
          + "."
          + TurbineUserGroupRole.TURBINE_GROUP_PROPERTY
          + "."
          + TurbineGroup.GROUP_NAME_PROPERTY, target_group_name);
      query.andQualifier(exp);
    }

    if ((target_keyword != null) && (!target_keyword.getValue().equals(""))) {
      // 選択したキーワードを指定する．
      String keyword = "%" + target_keyword.getValue() + "%";
      Expression exp =
        ExpressionFactory.likeExp(EipTTest.TEST_NAME_PROPERTY, keyword);
      Expression exp2 =
        ExpressionFactory.likeExp(EipTTest.NOTE_PROPERTY, keyword);

      /* フルネーム対応 */
      String first_name = keyword;
      String last_name = keyword;
      char[] c = { '\u3000' };
      String wspace = new String(c);
      String keyword2 = target_keyword.getValue().replaceAll(wspace, " ");
      if (keyword2.contains(" ")) {
        String[] spstr = keyword2.split(" ");
        if (spstr.length == 2) {
          first_name = "%" + spstr[1] + "%";
          last_name = "%" + spstr[0] + "%";
        }
      }

      Expression exp3 =
        ExpressionFactory.likeExp(EipTTest.TURBINE_USER_PROPERTY
          + "."
          + TurbineUser.FIRST_NAME_PROPERTY, first_name);
      Expression exp4 =
        ExpressionFactory.likeExp(EipTTest.TURBINE_USER_PROPERTY
          + "."
          + TurbineUser.FIRST_NAME_KANA_PROPERTY, first_name);
      Expression exp5 =
        ExpressionFactory.likeExp(EipTTest.TURBINE_USER_PROPERTY
          + "."
          + TurbineUser.LAST_NAME_PROPERTY, last_name);
      Expression exp6 =
        ExpressionFactory.likeExp(EipTTest.TURBINE_USER_PROPERTY
          + "."
          + TurbineUser.LAST_NAME_KANA_PROPERTY, last_name);

      query.andQualifier(exp
        .orExp(exp2)
        .orExp(exp3)
        .orExp(exp4)
        .orExp(exp5)
        .orExp(exp6));
    }

//    if ("list".equals(currentTab)) {
//      Expression exp3 =
//        ExpressionFactory.noMatchExp(EipTTest.STATE_PROPERTY, Short
//          .valueOf((short) 100));
//      query.andQualifier(exp3);
//    } else if ("complete".equals(currentTab)) {
//      Expression exp4 =
//        ExpressionFactory.matchExp(EipTTest.STATE_PROPERTY, Short
//          .valueOf((short) 100));
//      query.andQualifier(exp4);
//    }

    // 公開ならば無条件に閲覧
    // 非公開ならuserIDが一致していれば閲覧可能
//    Expression exp5 =
//      ExpressionFactory.matchExp(EipTTest.PUBLIC_FLAG_PROPERTY, "T");
//    if (target_user_id != null
//      && (target_user_id.equals("all") || target_user_id.equals(String
//        .valueOf(login_user_id)))) {
//      Expression exp6 =
//        ExpressionFactory.matchExp(EipTTest.PUBLIC_FLAG_PROPERTY, "F");
//      Expression exp7 =
//        ExpressionFactory.matchExp(EipTTest.USER_ID_PROPERTY, login_user_id);
//      query.andQualifier(exp5.orExp(exp6.andExp(exp7)));
//    } else {
//      query.andQualifier(exp5);
//    }

    return buildSelectQueryForFilter(query, rundata, context);
  }

  /**
   * パラメータをマップに変換します。
   *
   * @param key
   * @param val
   */
  @Override
  protected void parseFilterMap(String key, String val) {
    super.parseFilterMap(key, val);

    Set<String> unUse = new HashSet<String>();

    for (Entry<String, List<String>> pair : current_filterMap.entrySet()) {
      if (pair.getValue().contains("0")) {
        unUse.add(pair.getKey());
      }
    }
    for (String unusekey : unUse) {
      current_filterMap.remove(unusekey);
    }
  }

  // :TEST
  @Override
  protected SelectQuery<EipTTest> buildSelectQueryForFilter(
      SelectQuery<EipTTest> query, RunData rundata, Context context) {

    super.buildSelectQueryForFilter(query, rundata, context);

    if (current_filterMap.containsKey("post")) {
      // 部署を含んでいる場合デフォルトとは別にフィルタを用意

      List<String> postIds = current_filterMap.get("post");

      HashSet<Integer> userIds = new HashSet<Integer>();
      for (String post : postIds) {
        List<Integer> userId = ALEipUtils.getUserIds(post);
        userIds.addAll(userId);
      }
      if (userIds.isEmpty()) {
        userIds.add(-1);
      }
      Expression exp =
        ExpressionFactory.inExp(EipTTest.USER_ID_PROPERTY, userIds);
      query.andQualifier(exp);
    }

    // String search = ALEipUtils.getTemp(rundata, context, LIST_SEARCH_STR);
    // if (search != null && !"".equals(search)) {
    // current_search = search;
    // Expression ex1 =
    // ExpressionFactory.likeExp(EipTTest.NOTE_PROPERTY, "%" + search + "%");
    // Expression ex2 =
    // ExpressionFactory.likeExp(EipTTest.TODO_NAME_PROPERTY, "%"
    // + search
    // + "%");
    // SelectQuery<EipTTest> q = Database.query(EipTTest.class);
    // q.andQualifier(ex1.orExp(ex2));
    // List<EipTTest> queryList = q.fetchList();
    // List<Integer> resultid = new ArrayList<Integer>();
    // for (EipTTest item : queryList) {
    // if (item.getParentId() != 0 && !resultid.contains(item.getParentId())) {
    // resultid.add(item.getParentId());
    // } else if (!resultid.contains(item.getTestId())) {
    // resultid.add(item.getTestId());
    // }
    // }
    // if (resultid.size() == 0) {
    // // 検索結果がないことを示すために-1を代入
    // resultid.add(-1);
    // }
    // Expression ex =
    // ExpressionFactory.inDbExp(EipTTest.TODO_ID_PK_COLUMN, resultid);
    // query.andQualifier(ex);
    // }
    return query;
  }

  /**
   * ResultData に値を格納して返します。（一覧データ） <BR>
   *
   * @param obj
   * @return
   */
  @Override
  protected Object getResultData(EipTTest record) {
    try {
      // For data inconsistencies
//      EipTTestCategory category = record.getEipTTestCategory();
//      if (category == null) {
//        return null;
//      }

      TestResultData rd = new TestResultData();
      rd.initField();
      rd.setTestId(record.getTestId().intValue());
//      rd.setCategoryId((int) category.getCategoryId().longValue());
//      rd.setCategoryName(ALCommonUtils.compressString(record
//        .getEipTTestCategory()
//        .getCategoryName(), getStrLength()));
//      rd.setUserName(ALEipUtils
//        .getALEipUser(record.getUserId())
//        .getAliasName()
//        .getValue());
      rd.setTestName(ALCommonUtils.compressString(
        record.getTestName(),
        getStrLength()));
      rd.setUrl(ALCommonUtils.compressString(
    	        record.getUrl(),
    	        getStrLength()));
      rd.setFiveValueString((ALCommonUtils.getFiveValueString(record.getFiveValue().intValue())));
      rd.setFiveValueImage((ALCommonUtils.getFiveValueImage(record.getFiveValue().intValue())));
//      if (!TestUtils.isEmptyDate(record.getStartDate())) {
//        rd.setStartDate(ALDateUtil
//          .format(record.getStartDate(), "yyyy年M月d日(E)"));
//      }
//      if (!TestUtils.isEmptyDate(record.getEndDate())) {
//        rd.setEndDate(ALDateUtil.format(record.getEndDate(), "yyyy年M月d日(E)"));
//      }
//      rd.setState(record.getState().intValue());
//      rd.setStateImage(TestUtils.getStateImage(record.getState().intValue()));
//      rd.setStateString(TestUtils.getStateString(record.getState().intValue()));
//      rd.setPriority(record.getPriority().intValue());
//      rd.setPriorityImage(TestUtils.getPriorityImage(record
//        .getPriority()
//        .intValue()));
//      rd.setPriorityString(TestUtils.getPriorityString(record
//        .getPriority()
//        .intValue()));
      rd.setUpdateDate(record.getUpdateDate());

      // 公開/非公開を設定する．
//      rd.setPublicFlag("T".equals(record.getPublicFlag()));
      // 期限状態を設定する．
//      rd.setLimitState(TestUtils.getLimitState(record.getEndDate()));

      rd.setAclEditTestOther(hasAclEditTestOther);
      rd.setAclDeleteTestOther(hasAclDeleteTestOther);
      return rd;
    } catch (Exception ex) {
      logger.error("test", ex);
      return null;
    }
  }

  /**
   * 詳細データを取得します。 <BR>
   *
   * @param rundata
   * @param context
   * @return
   */
  @Override
  public EipTTest selectDetail(RunData rundata, Context context)
      throws ALPageNotFoundException {
    String js_peid = rundata.getParameters().getString("sch");

    if (js_peid != null && !js_peid.equals("")) {
      // ポートレット Scheduleのへのリンクを取得する．
      scheduleUrl = getPortletURItoSchedule(rundata, js_peid);
    }

    try {
      EipTTest test = TestUtils.getEipTTest(rundata, context, true);
      return test;
    } catch (ALPageNotFoundException pageNotFound) {
      throw pageNotFound;
    }
  }

  protected String getPortletURItoSchedule(RunData rundata,
      String schedulePortletId) {
    int prev = rundata.getParameters().getInt("prev");
    if (prev == JetspeedRunData.MAXIMIZE) {
      // 最大化画面のとき
      return ALEipUtils.getPortletURI(rundata, schedulePortletId);
    } else {
      // ノーマル画面のとき
      return ALEipUtils.getPortletURItoTopPage(rundata, schedulePortletId);
    }
  }

  /**
   * ResultData に値を格納して返します。（詳細データ） <BR>
   *
   * @param obj
   * @return
   */
  @Override
  protected Object getResultDataDetail(EipTTest record) {
    try {
      TestResultData rd = new TestResultData();
      rd.initField();
      rd.setTestName(record.getTestName());
      rd.setTestId(record.getTestId().longValue());
//      rd
//        .setCategoryId(record.getEipTTestCategory().getCategoryId().longValue());
//      rd.setCategoryName(record.getEipTTestCategory().getCategoryName());
//      rd.setUserName(ALEipUtils
//        .getALEipUser(record.getUserId())
//        .getAliasName()
//        .getValue());
//      if (!TestUtils.isEmptyDate(record.getStartDate())) {
//        rd.setStartDate(ALDateUtil
//          .format(record.getStartDate(), "yyyy年M月d日(E)"));
//      }
//      if (!TestUtils.isEmptyDate(record.getEndDate())) {
//        rd.setEndDate(ALDateUtil.format(record.getEndDate(), "yyyy年M月d日(E)"));
//      }
//      rd.setStateString(TestUtils.getStateString(record.getState().intValue()));
//      rd.setPriorityString(TestUtils.getPriorityString(record
//        .getPriority()
//        .intValue()));
      rd.setNote(record.getNote());
      rd.setUrl(record.getUrl());
      rd.setUrlHtml(record.getUrl());
      rd.setFiveValueString((ALCommonUtils.getFiveValueString(record.getFiveValue().intValue())));
      rd.setFiveValueImage((ALCommonUtils.getFiveValueImage(record.getFiveValue().intValue())));
//      rd.setCreateUserName(ALEipUtils
//        .getALEipUser(record.getCreateUserId())
//        .getAliasName()
//        .getValue());
      // 公開/非公開を設定する．
//      rd.setPublicFlag("T".equals(record.getPublicFlag()));
//      rd.setAddonScheduleFlg("T".equals(record.getAddonScheduleFlg()));
      rd.setCreateDate(ALDateUtil
        .format(record.getCreateDate(), "yyyy年M月d日(E)"));
      rd.setUpdateDate(record.getUpdateDate());

      // 自身のTestかを設定する
//      rd.setIsSelfTest(record.getUserId() == login_user_id);

      rd.setAclEditTestOther(hasAclEditTestOther);
      rd.setAclDeleteTestOther(hasAclDeleteTestOther);
      return rd;
    } catch (Exception ex) {
      logger.error("test", ex);
      return null;
    }
  }

  /**
   *
   * @return
   */
//  public List<TestCategoryResultData> getCategoryList() {
//    return categoryList;
//  }

  /**
   * 現在選択されているタブを取得します。 <BR>
   *
   * @return
   */
  public String getCurrentTab() {
    return currentTab;
  }

  /**
   * Test の総数を返す． <BR>
   *
   * @return
   */
  public int getTestSum() {
    return testSum;
  }

  /**
   * @return
   *
   */
  @Override
  protected Attributes getColumnMap() {
    Attributes map = new Attributes();
    map.putValue("test_name", EipTTest.TEST_NAME_PROPERTY);
//    map.putValue("state", EipTTest.STATE_PROPERTY);
//    map.putValue("priority", EipTTest.PRIORITY_PROPERTY);
//    map.putValue("end_date", EipTTest.END_DATE_PROPERTY);
//    map.putValue("category_name", EipTTest.EIP_TTEST_CATEGORY_PROPERTY
//      + "."
//      + EipTTestCategory.CATEGORY_NAME_PROPERTY);
//    map.putValue("category", EipTTestCategory.CATEGORY_ID_PK_COLUMN);
//    map.putValue("user_name", EipTTestCategory.TURBINE_USER_PROPERTY
//      + "."
//      + TurbineUser.LAST_NAME_KANA_PROPERTY);
    map.putValue(EipTTest.UPDATE_DATE_PROPERTY, EipTTest.UPDATE_DATE_PROPERTY);
    return map;
  }

  /**
   *
   * @param id
   * @return
   */
  public boolean isMatch(int id1, long id2) {
    return id1 == (int) id2;
  }

  public String getScheduleUrl() {
    return scheduleUrl;
  }

  /**
   * @return target_group_name
   */
  public String getTargetGroupName() {
    return target_group_name;
  }

  public void setTargetGroupName(String target_group_name) {
    this.target_group_name = target_group_name;
  }

  /**
   * @return target_user_id
   */
  public String getTargetUserId() {
    return target_user_id;
  }

  /**
   *
   * @param groupname
   * @return
   */
  public List<ALEipUser> getUsers() {
    if ((target_group_name != null)
      && (!target_group_name.equals(""))
      && (!target_group_name.equals("all"))) {
      return ALEipUtils.getUsers(target_group_name);
    } else {
      return ALEipUtils.getUsers("LoginUser");
    }
  }

  /**
   * @param target_user_id
   *          セットする target_user_id
   */
  public void setTargetUserId(String target_user_id) {
    this.target_user_id = target_user_id;
  }

  /**
   * アクセス権限チェック用メソッド。<br />
   * アクセス権限の機能名を返します。
   *
   * @return
   */
  @Override
  public String getAclPortletFeature() {
    return ALAccessControlConstants.POERTLET_FEATURE_TODO_TODO_SELF;
  }

  public void setMyGroupList(List<ALEipGroup> myGroupList) {
    this.myGroupList = myGroupList;
  }

  public List<ALEipGroup> getMyGroupList() {
    return myGroupList;
  }

  /**
   * @return target_keyword
   */
  public ALStringField getTargetKeyword() {
    return target_keyword;
  }

  /**
   * @return table_colum_num
   */
  public int getTableColumNum() {
    return table_colum_num;
  }

  /**
   * @param table_colum_num
   *          セットする
   */
  public void setTableColumNum(int table_colum_num) {
    this.table_colum_num = table_colum_num;
  }

  /**
   * 部署一覧を取得します
   *
   * @return postList
   */
  public List<ALEipGroup> getPostList() {
    return postList;
  }

  /**
   * 部署の一覧を取得する．
   *
   * @return
   */
  public Map<Integer, ALEipPost> getPostMap() {
    return ALEipManager.getInstance().getPostMap();
  }

  public void setFiltersPSML(VelocityPortlet portlet, Context context,
      RunData rundata) {
    ALEipUtils.setTemp(rundata, context, LIST_FILTER_STR, portlet
      .getPortletConfig()
      .getInitParameter("p12f-filters"));

    ALEipUtils.setTemp(rundata, context, LIST_FILTER_TYPE_STR, portlet
      .getPortletConfig()
      .getInitParameter("p12g-filtertypes"));
  }

//  public String getCategoryId() {
//    return categoryId;
//  }
}
