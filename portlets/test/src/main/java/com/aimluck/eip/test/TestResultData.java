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

import java.util.Date;

import com.aimluck.commons.field.ALDateTimeField;
import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.common.ALData;
import com.aimluck.eip.util.ALCommonUtils;
import com.aimluck.eip.util.ALEipUtils;

/**
 * TestのResultDataです。 <BR>
 *
 */
public class TestResultData implements ALData {

  /** Test ID */
  private ALNumberField test_id;

  /** カテゴリID */
//  private ALNumberField category_id;

  /** 状態 */
//  private ALNumberField state;

  /** 優先度 */
//  private ALNumberField priority;

  /** 担当者 */
//  private ALStringField user_name;

  /** 作成者 */
//  private ALStringField create_user_name;

  /** タイトル */
  private ALStringField test_name;

  /** カテゴリ名 */
//  private ALStringField category_name;

  /** 開始日 */
//  private ALStringField start_date;

  /** 締切日 */
//  private ALStringField end_date;

  /** メモ */
  private ALStringField note;

  /** URL */
  private ALStringField url;

  /** 評価 */
  private ALNumberField fiveValue;

  /** 評価(文字列) */
  private ALStringField fiveValueString;

  /** 評価(画像文字列) */
  private ALStringField fiveValueImage;

  /** 公開/非公開フラグ */
//  private boolean is_public;

  /** スケジュール表示フラグ */
//  private boolean addon_schedule_flg;

  /** 優先度画像名 */
//  private ALStringField priority_image;

  /** 優先度（文字列） */
//  private ALStringField priority_string;

  /** 状態画像名 */
//  private ALStringField state_image;

  /** 状態（文字列） */
//  private ALStringField state_string;

  /** 登録日 */
  private ALStringField create_date;

  /** 更新日 */
  private ALDateTimeField update_date;

  /** ユーザID */
  private ALStringField user_id;

  /**
   * 期限状態（期限前/期限当日/期限後）． <br>
   * クラス TestUtils の変数 LIMIT_STATE_BEFORE，LIMIT_STATE_TODAY，LIMIT_STATE_AFTER
   * を参照．
   */
  private ALNumberField limit_state;

  private boolean is_self_test;

  private boolean hasAclEditTestOther;

  private boolean hasAclDeleteTestOther;

  /**
   *
   *
   */
  @Override
  public void initField() {
    test_id = new ALNumberField();
//    category_id = new ALNumberField();
//    state = new ALNumberField();
//    priority = new ALNumberField();
//    user_name = new ALStringField();
    test_name = new ALStringField();
//    category_name = new ALStringField();
    note = new ALStringField();
    note.setTrim(false);
    url = new ALStringField();
    url.setTrim(false);
    fiveValue = new ALNumberField();
    fiveValueString = new ALStringField();
    fiveValueImage = new ALStringField();
//    start_date = new ALStringField();
//    end_date = new ALStringField();
//    priority_image = new ALStringField();
//    priority_string = new ALStringField();
//    state_image = new ALStringField();
//    state_string = new ALStringField();
    create_date = new ALStringField();
    update_date = new ALDateTimeField();
//    limit_state = new ALNumberField();
//    create_user_name = new ALStringField();
//    is_public = true;
//    addon_schedule_flg = true;
    is_self_test = false;
  }

  /**
   * @return
   */
//  public ALNumberField getCategoryId() {
//    return category_id;
//  }

  /**
   * @return
   */
//  public String getCategoryName() {
//    return category_name.toString();
//  }
//
//  public String getCategoryNameHtml() {
//    return ALCommonUtils.replaceToAutoCR(category_name.toString());
//  }

  /**
   * @return
   */
//  public ALStringField getEndDate() {
//    return end_date;
//  }

  /**
   * @return
   */
//  public ALNumberField getPriority() {
//    return priority;
//  }

  /**
   * @return
   */
//  public ALNumberField getState() {
//    return state;
//  }

  /**
   * @return
   */
  public ALNumberField getTestId() {
    return test_id;
  }

  /**
   * @return
   */
  public String getTestName() {
    return ALCommonUtils.replaceToAutoCR(test_name.toString());
  }

  /**
   * @param i
   */
//  public void setCategoryId(long i) {
//    category_id.setValue(i);
//  }

  /**
   * @param string
   */
//  public void setCategoryName(String string) {
//    category_name.setValue(string);
//  }

  /**
   * @param string
   */
//  public void setEndDate(String string) {
//    end_date.setValue(string);
//  }

  /**
   * @param i
   */
//  public void setPriority(int i) {
//    priority.setValue(i);
//  }

  /**
   * @param i
   */
//  public void setState(int i) {
//    state.setValue(i);
//  }

  /**
   * @param i
   */
  public void setTestId(long i) {
    test_id.setValue(i);
  }

  /**
   * @param i
   */
  public void setFiveValue(long i) {
    fiveValue.setValue(i);
  }

  /**
   * @param string
   */
  public void setFiveValueString(String string) {
    fiveValueString.setValue(string);
  }

  /**
   * @param string
   */
  public void setFiveValueImage(String string) {
    fiveValueImage.setValue(string);
  }
  /**
   * @param string
   */
  public void setTestName(String string) {
    test_name.setValue(string);
  }

  /**
   * @return
   */
  public String getNote() {
    return ALEipUtils.getMessageList(note.getValue());
  }

  /**
   * @returnただの数字
   */
  public long getFiveValue() {
    return fiveValue.getValue();
  }

  /**
   * @return評価文字列
   */
  public String getFiveValueString() {
	    return fiveValueString.getValue();
	  }

  /**
   * @return評価画像文字列
   */
  public String getFiveValueImage() {
	    return fiveValueImage.getValue();
	  }

  /**
   * @return
   */
  public String getUrlHtml() {
    return url.getValue();
  }

  /**
   * @return
   */
  public String getUrl() {
    return ALEipUtils.getMessageList(url.getValue());
  }

  /**
   * @return
   */
//  public ALStringField getStartDate() {
//    return start_date;
//  }

  /**
   * 公開/非公開フラグ．
   *
   * @return
   */
//  public boolean isPublic() {
//    return is_public;
//  }

  /**
   * @param string
   */
  public void setNote(String string) {
    note.setValue(string);
  }

  /**
   * @param string
   */
  public void setUrl(String string) {
    url.setValue(string);
  }

  /**
   * @param string
   */
  public void setUrlHtml(String string) {
    url.setValue(string);
  }

  /**
   * @return
   */
//  public void setPublicFlag(boolean bool) {
//    is_public = bool;
//  }

  /**
   * @param string
   */
//  public void setStartDate(String string) {
//    start_date.setValue(string);
//  }

  /**
   * @return
   */
//  public ALStringField getPriorityImage() {
//    return priority_image;
//  }

  /**
   * @return
   */
//  public ALStringField getPriorityString() {
//    return priority_string;
//  }

  /**
   * @param string
   */
//  public void setPriorityImage(String string) {
//    priority_image.setValue(string);
//  }

  /**
   * @param string
   */
//  public void setPriorityString(String string) {
//    priority_string.setValue(string);
//  }

  /**
   * @return
   */
//  public ALStringField getStateString() {
//    return state_string;
//  }

  /**
   * @param string
   */
//  public void setStateString(String string) {
//    state_string.setValue(string);
//  }

  /**
   * @param string
   */
//  public void setStateImage(String string) {
//    state_image.setValue(string);
//  }

  /**
   *
   * @return
   */
//  public ALStringField getStateImage() {
//    return state_image;
//  }

  /**
   * @return
   */
  public ALStringField getCreateDate() {
    return create_date;
  }

  /**
   * @return
   */
  public ALDateTimeField getUpdateDate() {
    return ALEipUtils.getFormattedTime(update_date);
  }

  /**
   * @param string
   */
  public void setCreateDate(String string) {
    create_date.setValue(string);
  }

  /**
   * @param string
   */
  public void setUpdateDate(Date date) {
    if (date == null) {
      return;
    }
    this.update_date.setValue(date);
  }

  /**
   *
   * @return
   */
//  public ALNumberField getLimitState() {
//    return limit_state;
//  }

  /**
   *
   * @param value
   */
//  public void setLimitState(int value) {
//    limit_state.setValue(value);
//  }

//  public void setAddonScheduleFlg(boolean bool) {
//    addon_schedule_flg = bool;
//  }

//  public boolean addonScheduleFlg() {
//    return addon_schedule_flg;
//  }

//  public void setUserName(String user_name) {
//    this.user_name.setValue(user_name);
//  }

//  public ALStringField getUserName() {
//    return user_name;
//  }

//  public String getUserNameHtml() {
//    return ALCommonUtils.replaceToAutoCR(user_name.toString());
//  }

//  public void setCreateUserName(String create_user_name) {
//    this.create_user_name.setValue(create_user_name);
//  }

//  public ALStringField getCreateUserName() {
//    return create_user_name;
//  }

//  public boolean isSelfTest() {
//    return is_self_test;
//  }

//  public void setIsSelfTest(boolean is_self_test) {
//    this.is_self_test = is_self_test;
//  }

  /**
   * hasAclEditTestOtherを取得します。
   *
   * @return hasAclEditTestOther
   */
  public boolean hasAclEditTestOther() {
    return hasAclEditTestOther;
  }

  /**
   * hasAclEditTestOtherを設定します。
   *
   * @param hasAclEditTestOther
   *          hasAclEditTestOther
   */
  public void setAclEditTestOther(boolean hasAclEditTestOther) {
    this.hasAclEditTestOther = hasAclEditTestOther;
  }

  /**
   * hasAclDeleteTestOtherを取得します。
   *
   * @return hasAclDeleteTestOther
   */
  public boolean hasAclDeleteTestOther() {
    return hasAclDeleteTestOther;
  }

  /**
   * hasAclDeleteTestOtherを設定します。
   *
   * @param hasAclDeleteTestOther
   *          hasAclDeleteTestOther
   */
  public void setAclDeleteTestOther(boolean hasAclDeleteTestOther) {
    this.hasAclDeleteTestOther = hasAclDeleteTestOther;
  }
}
