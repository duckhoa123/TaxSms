package com.vnnet.kpi.web.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SysMessagHdExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public SysMessagHdExample() {
        oredCriteria = new ArrayList<>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andMessHdIdIsNull() {
            addCriterion("mess_hd_id is null");
            return (Criteria) this;
        }

        public Criteria andMessHdIdIsNotNull() {
            addCriterion("mess_hd_id is not null");
            return (Criteria) this;
        }

        public Criteria andMessHdIdEqualTo(Long value) {
            addCriterion("mess_hd_id =", value, "messHdId");
            return (Criteria) this;
        }

        public Criteria andMessHdIdNotEqualTo(Long value) {
            addCriterion("mess_hd_id <>", value, "messHdId");
            return (Criteria) this;
        }

        public Criteria andMessHdIdGreaterThan(Long value) {
            addCriterion("mess_hd_id >", value, "messHdId");
            return (Criteria) this;
        }

        public Criteria andMessHdIdGreaterThanOrEqualTo(Long value) {
            addCriterion("mess_hd_id >=", value, "messHdId");
            return (Criteria) this;
        }

        public Criteria andMessHdIdLessThan(Long value) {
            addCriterion("mess_hd_id <", value, "messHdId");
            return (Criteria) this;
        }

        public Criteria andMessHdIdLessThanOrEqualTo(Long value) {
            addCriterion("mess_hd_id <=", value, "messHdId");
            return (Criteria) this;
        }

        public Criteria andMessHdIdIn(List<Long> values) {
            addCriterion("mess_hd_id in", values, "messHdId");
            return (Criteria) this;
        }

        public Criteria andMessHdIdNotIn(List<Long> values) {
            addCriterion("mess_hd_id not in", values, "messHdId");
            return (Criteria) this;
        }

        public Criteria andMessHdIdBetween(Long value1, Long value2) {
            addCriterion("mess_hd_id between", value1, value2, "messHdId");
            return (Criteria) this;
        }

        public Criteria andMessHdIdNotBetween(Long value1, Long value2) {
            addCriterion("mess_hd_id not between", value1, value2, "messHdId");
            return (Criteria) this;
        }

        public Criteria andTitleIsNull() {
            addCriterion("title is null");
            return (Criteria) this;
        }

        public Criteria andTitleIsNotNull() {
            addCriterion("title is not null");
            return (Criteria) this;
        }

        public Criteria andTitleEqualTo(String value) {
            addCriterion("title =", value, "title");
            return (Criteria) this;
        }

        public Criteria andTitleNotEqualTo(String value) {
            addCriterion("title <>", value, "title");
            return (Criteria) this;
        }

        public Criteria andTitleGreaterThan(String value) {
            addCriterion("title >", value, "title");
            return (Criteria) this;
        }

        public Criteria andTitleGreaterThanOrEqualTo(String value) {
            addCriterion("title >=", value, "title");
            return (Criteria) this;
        }

        public Criteria andTitleLessThan(String value) {
            addCriterion("title <", value, "title");
            return (Criteria) this;
        }

        public Criteria andTitleLessThanOrEqualTo(String value) {
            addCriterion("title <=", value, "title");
            return (Criteria) this;
        }

        public Criteria andTitleLike(String value) {
            addCriterion("title like", value, "title");
            return (Criteria) this;
        }

        public Criteria andTitleNotLike(String value) {
            addCriterion("title not like", value, "title");
            return (Criteria) this;
        }

        public Criteria andTitleIn(List<String> values) {
            addCriterion("title in", values, "title");
            return (Criteria) this;
        }

        public Criteria andTitleNotIn(List<String> values) {
            addCriterion("title not in", values, "title");
            return (Criteria) this;
        }

        public Criteria andTitleBetween(String value1, String value2) {
            addCriterion("title between", value1, value2, "title");
            return (Criteria) this;
        }

        public Criteria andTitleNotBetween(String value1, String value2) {
            addCriterion("title not between", value1, value2, "title");
            return (Criteria) this;
        }

        public Criteria andSchedulerDateTimeIsNull() {
            addCriterion("scheduler_date_time is null");
            return (Criteria) this;
        }

        public Criteria andSchedulerDateTimeIsNotNull() {
            addCriterion("scheduler_date_time is not null");
            return (Criteria) this;
        }

        public Criteria andSchedulerDateTimeEqualTo(Date value) {
            addCriterion("scheduler_date_time =", value, "schedulerDateTime");
            return (Criteria) this;
        }

        public Criteria andSchedulerDateTimeNotEqualTo(Date value) {
            addCriterion("scheduler_date_time <>", value, "schedulerDateTime");
            return (Criteria) this;
        }

        public Criteria andSchedulerDateTimeGreaterThan(Date value) {
            addCriterion("scheduler_date_time >", value, "schedulerDateTime");
            return (Criteria) this;
        }

        public Criteria andSchedulerDateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("scheduler_date_time >=", value, "schedulerDateTime");
            return (Criteria) this;
        }

        public Criteria andSchedulerDateTimeLessThan(Date value) {
            addCriterion("scheduler_date_time <", value, "schedulerDateTime");
            return (Criteria) this;
        }

        public Criteria andSchedulerDateTimeLessThanOrEqualTo(Date value) {
            addCriterion("scheduler_date_time <=", value, "schedulerDateTime");
            return (Criteria) this;
        }

        public Criteria andSchedulerDateTimeIn(List<Date> values) {
            addCriterion("scheduler_date_time in", values, "schedulerDateTime");
            return (Criteria) this;
        }

        public Criteria andSchedulerDateTimeNotIn(List<Date> values) {
            addCriterion("scheduler_date_time not in", values, "schedulerDateTime");
            return (Criteria) this;
        }

        public Criteria andSchedulerDateTimeBetween(Date value1, Date value2) {
            addCriterion("scheduler_date_time between", value1, value2, "schedulerDateTime");
            return (Criteria) this;
        }

        public Criteria andSchedulerDateTimeNotBetween(Date value1, Date value2) {
            addCriterion("scheduler_date_time not between", value1, value2, "schedulerDateTime");
            return (Criteria) this;
        }

        public Criteria andSmsTemplateIsNull() {
            addCriterion("sms_template is null");
            return (Criteria) this;
        }

        public Criteria andSmsTemplateIsNotNull() {
            addCriterion("sms_template is not null");
            return (Criteria) this;
        }

        public Criteria andSmsTemplateEqualTo(String value) {
            addCriterion("sms_template =", value, "smsTemplate");
            return (Criteria) this;
        }

        public Criteria andSmsTemplateNotEqualTo(String value) {
            addCriterion("sms_template <>", value, "smsTemplate");
            return (Criteria) this;
        }

        public Criteria andSmsTemplateGreaterThan(String value) {
            addCriterion("sms_template >", value, "smsTemplate");
            return (Criteria) this;
        }

        public Criteria andSmsTemplateGreaterThanOrEqualTo(String value) {
            addCriterion("sms_template >=", value, "smsTemplate");
            return (Criteria) this;
        }

        public Criteria andSmsTemplateLessThan(String value) {
            addCriterion("sms_template <", value, "smsTemplate");
            return (Criteria) this;
        }

        public Criteria andSmsTemplateLessThanOrEqualTo(String value) {
            addCriterion("sms_template <=", value, "smsTemplate");
            return (Criteria) this;
        }

        public Criteria andSmsTemplateLike(String value) {
            addCriterion("sms_template like", value, "smsTemplate");
            return (Criteria) this;
        }

        public Criteria andSmsTemplateNotLike(String value) {
            addCriterion("sms_template not like", value, "smsTemplate");
            return (Criteria) this;
        }

        public Criteria andSmsTemplateIn(List<String> values) {
            addCriterion("sms_template in", values, "smsTemplate");
            return (Criteria) this;
        }

        public Criteria andSmsTemplateNotIn(List<String> values) {
            addCriterion("sms_template not in", values, "smsTemplate");
            return (Criteria) this;
        }

        public Criteria andSmsTemplateBetween(String value1, String value2) {
            addCriterion("sms_template between", value1, value2, "smsTemplate");
            return (Criteria) this;
        }

        public Criteria andSmsTemplateNotBetween(String value1, String value2) {
            addCriterion("sms_template not between", value1, value2, "smsTemplate");
            return (Criteria) this;
        }

        public Criteria andCreateByIsNull() {
            addCriterion("create_by is null");
            return (Criteria) this;
        }

        public Criteria andCreateByIsNotNull() {
            addCriterion("create_by is not null");
            return (Criteria) this;
        }

        public Criteria andCreateByEqualTo(String value) {
            addCriterion("create_by =", value, "createBy");
            return (Criteria) this;
        }

        public Criteria andCreateByNotEqualTo(String value) {
            addCriterion("create_by <>", value, "createBy");
            return (Criteria) this;
        }

        public Criteria andCreateByGreaterThan(String value) {
            addCriterion("create_by >", value, "createBy");
            return (Criteria) this;
        }

        public Criteria andCreateByGreaterThanOrEqualTo(String value) {
            addCriterion("create_by >=", value, "createBy");
            return (Criteria) this;
        }

        public Criteria andCreateByLessThan(String value) {
            addCriterion("create_by <", value, "createBy");
            return (Criteria) this;
        }

        public Criteria andCreateByLessThanOrEqualTo(String value) {
            addCriterion("create_by <=", value, "createBy");
            return (Criteria) this;
        }

        public Criteria andCreateByLike(String value) {
            addCriterion("create_by like", value, "createBy");
            return (Criteria) this;
        }

        public Criteria andCreateByNotLike(String value) {
            addCriterion("create_by not like", value, "createBy");
            return (Criteria) this;
        }

        public Criteria andCreateByIn(List<String> values) {
            addCriterion("create_by in", values, "createBy");
            return (Criteria) this;
        }

        public Criteria andCreateByNotIn(List<String> values) {
            addCriterion("create_by not in", values, "createBy");
            return (Criteria) this;
        }

        public Criteria andCreateByBetween(String value1, String value2) {
            addCriterion("create_by between", value1, value2, "createBy");
            return (Criteria) this;
        }

        public Criteria andCreateByNotBetween(String value1, String value2) {
            addCriterion("create_by not between", value1, value2, "createBy");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNull() {
            addCriterion("create_time is null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNotNull() {
            addCriterion("create_time is not null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeEqualTo(Date value) {
            addCriterion("create_time =", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotEqualTo(Date value) {
            addCriterion("create_time <>", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThan(Date value) {
            addCriterion("create_time >", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("create_time >=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThan(Date value) {
            addCriterion("create_time <", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThanOrEqualTo(Date value) {
            addCriterion("create_time <=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIn(List<Date> values) {
            addCriterion("create_time in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotIn(List<Date> values) {
            addCriterion("create_time not in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeBetween(Date value1, Date value2) {
            addCriterion("create_time between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotBetween(Date value1, Date value2) {
            addCriterion("create_time not between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andLastUpdateByIsNull() {
            addCriterion("last_update_by is null");
            return (Criteria) this;
        }

        public Criteria andLastUpdateByIsNotNull() {
            addCriterion("last_update_by is not null");
            return (Criteria) this;
        }

        public Criteria andLastUpdateByEqualTo(String value) {
            addCriterion("last_update_by =", value, "lastUpdateBy");
            return (Criteria) this;
        }

        public Criteria andLastUpdateByNotEqualTo(String value) {
            addCriterion("last_update_by <>", value, "lastUpdateBy");
            return (Criteria) this;
        }

        public Criteria andLastUpdateByGreaterThan(String value) {
            addCriterion("last_update_by >", value, "lastUpdateBy");
            return (Criteria) this;
        }

        public Criteria andLastUpdateByGreaterThanOrEqualTo(String value) {
            addCriterion("last_update_by >=", value, "lastUpdateBy");
            return (Criteria) this;
        }

        public Criteria andLastUpdateByLessThan(String value) {
            addCriterion("last_update_by <", value, "lastUpdateBy");
            return (Criteria) this;
        }

        public Criteria andLastUpdateByLessThanOrEqualTo(String value) {
            addCriterion("last_update_by <=", value, "lastUpdateBy");
            return (Criteria) this;
        }

        public Criteria andLastUpdateByLike(String value) {
            addCriterion("last_update_by like", value, "lastUpdateBy");
            return (Criteria) this;
        }

        public Criteria andLastUpdateByNotLike(String value) {
            addCriterion("last_update_by not like", value, "lastUpdateBy");
            return (Criteria) this;
        }

        public Criteria andLastUpdateByIn(List<String> values) {
            addCriterion("last_update_by in", values, "lastUpdateBy");
            return (Criteria) this;
        }

        public Criteria andLastUpdateByNotIn(List<String> values) {
            addCriterion("last_update_by not in", values, "lastUpdateBy");
            return (Criteria) this;
        }

        public Criteria andLastUpdateByBetween(String value1, String value2) {
            addCriterion("last_update_by between", value1, value2, "lastUpdateBy");
            return (Criteria) this;
        }

        public Criteria andLastUpdateByNotBetween(String value1, String value2) {
            addCriterion("last_update_by not between", value1, value2, "lastUpdateBy");
            return (Criteria) this;
        }

        public Criteria andLastUpdateTimeIsNull() {
            addCriterion("last_update_time is null");
            return (Criteria) this;
        }

        public Criteria andLastUpdateTimeIsNotNull() {
            addCriterion("last_update_time is not null");
            return (Criteria) this;
        }

        public Criteria andLastUpdateTimeEqualTo(Date value) {
            addCriterion("last_update_time =", value, "lastUpdateTime");
            return (Criteria) this;
        }

        public Criteria andLastUpdateTimeNotEqualTo(Date value) {
            addCriterion("last_update_time <>", value, "lastUpdateTime");
            return (Criteria) this;
        }

        public Criteria andLastUpdateTimeGreaterThan(Date value) {
            addCriterion("last_update_time >", value, "lastUpdateTime");
            return (Criteria) this;
        }

        public Criteria andLastUpdateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("last_update_time >=", value, "lastUpdateTime");
            return (Criteria) this;
        }

        public Criteria andLastUpdateTimeLessThan(Date value) {
            addCriterion("last_update_time <", value, "lastUpdateTime");
            return (Criteria) this;
        }

        public Criteria andLastUpdateTimeLessThanOrEqualTo(Date value) {
            addCriterion("last_update_time <=", value, "lastUpdateTime");
            return (Criteria) this;
        }

        public Criteria andLastUpdateTimeIn(List<Date> values) {
            addCriterion("last_update_time in", values, "lastUpdateTime");
            return (Criteria) this;
        }

        public Criteria andLastUpdateTimeNotIn(List<Date> values) {
            addCriterion("last_update_time not in", values, "lastUpdateTime");
            return (Criteria) this;
        }

        public Criteria andLastUpdateTimeBetween(Date value1, Date value2) {
            addCriterion("last_update_time between", value1, value2, "lastUpdateTime");
            return (Criteria) this;
        }

        public Criteria andLastUpdateTimeNotBetween(Date value1, Date value2) {
            addCriterion("last_update_time not between", value1, value2, "lastUpdateTime");
            return (Criteria) this;
        }

        public Criteria andProgramIdIsNull() {
            addCriterion("program_id is null");
            return (Criteria) this;
        }

        public Criteria andProgramIdIsNotNull() {
            addCriterion("program_id is not null");
            return (Criteria) this;
        }

        public Criteria andProgramIdEqualTo(Long value) {
            addCriterion("program_id =", value, "programId");
            return (Criteria) this;
        }

        public Criteria andProgramIdNotEqualTo(Long value) {
            addCriterion("program_id <>", value, "programId");
            return (Criteria) this;
        }

        public Criteria andProgramIdGreaterThan(Long value) {
            addCriterion("program_id >", value, "programId");
            return (Criteria) this;
        }

        public Criteria andProgramIdGreaterThanOrEqualTo(Long value) {
            addCriterion("program_id >=", value, "programId");
            return (Criteria) this;
        }

        public Criteria andProgramIdLessThan(Long value) {
            addCriterion("program_id <", value, "programId");
            return (Criteria) this;
        }

        public Criteria andProgramIdLessThanOrEqualTo(Long value) {
            addCriterion("program_id <=", value, "programId");
            return (Criteria) this;
        }

        public Criteria andProgramIdIn(List<Long> values) {
            addCriterion("program_id in", values, "programId");
            return (Criteria) this;
        }

        public Criteria andProgramIdNotIn(List<Long> values) {
            addCriterion("program_id not in", values, "programId");
            return (Criteria) this;
        }

        public Criteria andProgramIdBetween(Long value1, Long value2) {
            addCriterion("program_id between", value1, value2, "programId");
            return (Criteria) this;
        }

        public Criteria andProgramIdNotBetween(Long value1, Long value2) {
            addCriterion("program_id not between", value1, value2, "programId");
            return (Criteria) this;
        }

        public Criteria andDetailFileIsNull() {
            addCriterion("detail_file is null");
            return (Criteria) this;
        }

        public Criteria andDetailFileIsNotNull() {
            addCriterion("detail_file is not null");
            return (Criteria) this;
        }

        public Criteria andDetailFileEqualTo(String value) {
            addCriterion("detail_file =", value, "detailFile");
            return (Criteria) this;
        }

        public Criteria andDetailFileNotEqualTo(String value) {
            addCriterion("detail_file <>", value, "detailFile");
            return (Criteria) this;
        }

        public Criteria andDetailFileGreaterThan(String value) {
            addCriterion("detail_file >", value, "detailFile");
            return (Criteria) this;
        }

        public Criteria andDetailFileGreaterThanOrEqualTo(String value) {
            addCriterion("detail_file >=", value, "detailFile");
            return (Criteria) this;
        }

        public Criteria andDetailFileLessThan(String value) {
            addCriterion("detail_file <", value, "detailFile");
            return (Criteria) this;
        }

        public Criteria andDetailFileLessThanOrEqualTo(String value) {
            addCriterion("detail_file <=", value, "detailFile");
            return (Criteria) this;
        }

        public Criteria andDetailFileLike(String value) {
            addCriterion("detail_file like", value, "detailFile");
            return (Criteria) this;
        }

        public Criteria andDetailFileNotLike(String value) {
            addCriterion("detail_file not like", value, "detailFile");
            return (Criteria) this;
        }

        public Criteria andDetailFileIn(List<String> values) {
            addCriterion("detail_file in", values, "detailFile");
            return (Criteria) this;
        }

        public Criteria andDetailFileNotIn(List<String> values) {
            addCriterion("detail_file not in", values, "detailFile");
            return (Criteria) this;
        }

        public Criteria andDetailFileBetween(String value1, String value2) {
            addCriterion("detail_file between", value1, value2, "detailFile");
            return (Criteria) this;
        }

        public Criteria andDetailFileNotBetween(String value1, String value2) {
            addCriterion("detail_file not between", value1, value2, "detailFile");
            return (Criteria) this;
        }

        public Criteria andContactFileIsNull() {
            addCriterion("contact_file is null");
            return (Criteria) this;
        }

        public Criteria andContactFileIsNotNull() {
            addCriterion("contact_file is not null");
            return (Criteria) this;
        }

        public Criteria andContactFileEqualTo(String value) {
            addCriterion("contact_file =", value, "contactFile");
            return (Criteria) this;
        }

        public Criteria andContactFileNotEqualTo(String value) {
            addCriterion("contact_file <>", value, "contactFile");
            return (Criteria) this;
        }

        public Criteria andContactFileGreaterThan(String value) {
            addCriterion("contact_file >", value, "contactFile");
            return (Criteria) this;
        }

        public Criteria andContactFileGreaterThanOrEqualTo(String value) {
            addCriterion("contact_file >=", value, "contactFile");
            return (Criteria) this;
        }

        public Criteria andContactFileLessThan(String value) {
            addCriterion("contact_file <", value, "contactFile");
            return (Criteria) this;
        }

        public Criteria andContactFileLessThanOrEqualTo(String value) {
            addCriterion("contact_file <=", value, "contactFile");
            return (Criteria) this;
        }

        public Criteria andContactFileLike(String value) {
            addCriterion("contact_file like", value, "contactFile");
            return (Criteria) this;
        }

        public Criteria andContactFileNotLike(String value) {
            addCriterion("contact_file not like", value, "contactFile");
            return (Criteria) this;
        }

        public Criteria andContactFileIn(List<String> values) {
            addCriterion("contact_file in", values, "contactFile");
            return (Criteria) this;
        }

        public Criteria andContactFileNotIn(List<String> values) {
            addCriterion("contact_file not in", values, "contactFile");
            return (Criteria) this;
        }

        public Criteria andContactFileBetween(String value1, String value2) {
            addCriterion("contact_file between", value1, value2, "contactFile");
            return (Criteria) this;
        }

        public Criteria andContactFileNotBetween(String value1, String value2) {
            addCriterion("contact_file not between", value1, value2, "contactFile");
            return (Criteria) this;
        }

        public Criteria andDelFlagIsNull() {
            addCriterion("del_flag is null");
            return (Criteria) this;
        }

        public Criteria andDelFlagIsNotNull() {
            addCriterion("del_flag is not null");
            return (Criteria) this;
        }

        public Criteria andDelFlagEqualTo(Byte value) {
            addCriterion("del_flag =", value, "delFlag");
            return (Criteria) this;
        }

        public Criteria andDelFlagNotEqualTo(Byte value) {
            addCriterion("del_flag <>", value, "delFlag");
            return (Criteria) this;
        }

        public Criteria andDelFlagGreaterThan(Byte value) {
            addCriterion("del_flag >", value, "delFlag");
            return (Criteria) this;
        }

        public Criteria andDelFlagGreaterThanOrEqualTo(Byte value) {
            addCriterion("del_flag >=", value, "delFlag");
            return (Criteria) this;
        }

        public Criteria andDelFlagLessThan(Byte value) {
            addCriterion("del_flag <", value, "delFlag");
            return (Criteria) this;
        }

        public Criteria andDelFlagLessThanOrEqualTo(Byte value) {
            addCriterion("del_flag <=", value, "delFlag");
            return (Criteria) this;
        }

        public Criteria andDelFlagIn(List<Byte> values) {
            addCriterion("del_flag in", values, "delFlag");
            return (Criteria) this;
        }

        public Criteria andDelFlagNotIn(List<Byte> values) {
            addCriterion("del_flag not in", values, "delFlag");
            return (Criteria) this;
        }

        public Criteria andDelFlagBetween(Byte value1, Byte value2) {
            addCriterion("del_flag between", value1, value2, "delFlag");
            return (Criteria) this;
        }

        public Criteria andDelFlagNotBetween(Byte value1, Byte value2) {
            addCriterion("del_flag not between", value1, value2, "delFlag");
            return (Criteria) this;
        }

        public Criteria andDelReasonIsNull() {
            addCriterion("del_reason is null");
            return (Criteria) this;
        }

        public Criteria andDelReasonIsNotNull() {
            addCriterion("del_reason is not null");
            return (Criteria) this;
        }

        public Criteria andDelReasonEqualTo(String value) {
            addCriterion("del_reason =", value, "delReason");
            return (Criteria) this;
        }

        public Criteria andDelReasonNotEqualTo(String value) {
            addCriterion("del_reason <>", value, "delReason");
            return (Criteria) this;
        }

        public Criteria andDelReasonGreaterThan(String value) {
            addCriterion("del_reason >", value, "delReason");
            return (Criteria) this;
        }

        public Criteria andDelReasonGreaterThanOrEqualTo(String value) {
            addCriterion("del_reason >=", value, "delReason");
            return (Criteria) this;
        }

        public Criteria andDelReasonLessThan(String value) {
            addCriterion("del_reason <", value, "delReason");
            return (Criteria) this;
        }

        public Criteria andDelReasonLessThanOrEqualTo(String value) {
            addCriterion("del_reason <=", value, "delReason");
            return (Criteria) this;
        }

        public Criteria andDelReasonLike(String value) {
            addCriterion("del_reason like", value, "delReason");
            return (Criteria) this;
        }

        public Criteria andDelReasonNotLike(String value) {
            addCriterion("del_reason not like", value, "delReason");
            return (Criteria) this;
        }

        public Criteria andDelReasonIn(List<String> values) {
            addCriterion("del_reason in", values, "delReason");
            return (Criteria) this;
        }

        public Criteria andDelReasonNotIn(List<String> values) {
            addCriterion("del_reason not in", values, "delReason");
            return (Criteria) this;
        }

        public Criteria andDelReasonBetween(String value1, String value2) {
            addCriterion("del_reason between", value1, value2, "delReason");
            return (Criteria) this;
        }

        public Criteria andDelReasonNotBetween(String value1, String value2) {
            addCriterion("del_reason not between", value1, value2, "delReason");
            return (Criteria) this;
        }

        public Criteria andPdfFileIsNull() {
            addCriterion("pdf_file is null");
            return (Criteria) this;
        }

        public Criteria andPdfFileIsNotNull() {
            addCriterion("pdf_file is not null");
            return (Criteria) this;
        }

        public Criteria andPdfFileEqualTo(String value) {
            addCriterion("pdf_file =", value, "pdfFile");
            return (Criteria) this;
        }

        public Criteria andPdfFileNotEqualTo(String value) {
            addCriterion("pdf_file <>", value, "pdfFile");
            return (Criteria) this;
        }

        public Criteria andPdfFileGreaterThan(String value) {
            addCriterion("pdf_file >", value, "pdfFile");
            return (Criteria) this;
        }

        public Criteria andPdfFileGreaterThanOrEqualTo(String value) {
            addCriterion("pdf_file >=", value, "pdfFile");
            return (Criteria) this;
        }

        public Criteria andPdfFileLessThan(String value) {
            addCriterion("pdf_file <", value, "pdfFile");
            return (Criteria) this;
        }

        public Criteria andPdfFileLessThanOrEqualTo(String value) {
            addCriterion("pdf_file <=", value, "pdfFile");
            return (Criteria) this;
        }

        public Criteria andPdfFileLike(String value) {
            addCriterion("pdf_file like", value, "pdfFile");
            return (Criteria) this;
        }

        public Criteria andPdfFileNotLike(String value) {
            addCriterion("pdf_file not like", value, "pdfFile");
            return (Criteria) this;
        }

        public Criteria andPdfFileIn(List<String> values) {
            addCriterion("pdf_file in", values, "pdfFile");
            return (Criteria) this;
        }

        public Criteria andPdfFileNotIn(List<String> values) {
            addCriterion("pdf_file not in", values, "pdfFile");
            return (Criteria) this;
        }

        public Criteria andPdfFileBetween(String value1, String value2) {
            addCriterion("pdf_file between", value1, value2, "pdfFile");
            return (Criteria) this;
        }

        public Criteria andPdfFileNotBetween(String value1, String value2) {
            addCriterion("pdf_file not between", value1, value2, "pdfFile");
            return (Criteria) this;
        }

        public Criteria andTitleLikeInsensitive(String value) {
            addCriterion("upper(title) like", value.toUpperCase(), "title");
            return (Criteria) this;
        }

        public Criteria andSmsTemplateLikeInsensitive(String value) {
            addCriterion("upper(sms_template) like", value.toUpperCase(), "smsTemplate");
            return (Criteria) this;
        }

        public Criteria andCreateByLikeInsensitive(String value) {
            addCriterion("upper(create_by) like", value.toUpperCase(), "createBy");
            return (Criteria) this;
        }

        public Criteria andLastUpdateByLikeInsensitive(String value) {
            addCriterion("upper(last_update_by) like", value.toUpperCase(), "lastUpdateBy");
            return (Criteria) this;
        }

        public Criteria andDetailFileLikeInsensitive(String value) {
            addCriterion("upper(detail_file) like", value.toUpperCase(), "detailFile");
            return (Criteria) this;
        }

        public Criteria andContactFileLikeInsensitive(String value) {
            addCriterion("upper(contact_file) like", value.toUpperCase(), "contactFile");
            return (Criteria) this;
        }

        public Criteria andDelReasonLikeInsensitive(String value) {
            addCriterion("upper(del_reason) like", value.toUpperCase(), "delReason");
            return (Criteria) this;
        }

        public Criteria andPdfFileLikeInsensitive(String value) {
            addCriterion("upper(pdf_file) like", value.toUpperCase(), "pdfFile");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {
        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}