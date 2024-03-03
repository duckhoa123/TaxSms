package com.vnnet.kpi.web.model;

import java.util.ArrayList;
import java.util.List;

public class SysMessagDtExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public SysMessagDtExample() {
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

        public Criteria andMessDtIdIsNull() {
            addCriterion("mess_dt_id is null");
            return (Criteria) this;
        }

        public Criteria andMessDtIdIsNotNull() {
            addCriterion("mess_dt_id is not null");
            return (Criteria) this;
        }

        public Criteria andMessDtIdEqualTo(Long value) {
            addCriterion("mess_dt_id =", value, "messDtId");
            return (Criteria) this;
        }

        public Criteria andMessDtIdNotEqualTo(Long value) {
            addCriterion("mess_dt_id <>", value, "messDtId");
            return (Criteria) this;
        }

        public Criteria andMessDtIdGreaterThan(Long value) {
            addCriterion("mess_dt_id >", value, "messDtId");
            return (Criteria) this;
        }

        public Criteria andMessDtIdGreaterThanOrEqualTo(Long value) {
            addCriterion("mess_dt_id >=", value, "messDtId");
            return (Criteria) this;
        }

        public Criteria andMessDtIdLessThan(Long value) {
            addCriterion("mess_dt_id <", value, "messDtId");
            return (Criteria) this;
        }

        public Criteria andMessDtIdLessThanOrEqualTo(Long value) {
            addCriterion("mess_dt_id <=", value, "messDtId");
            return (Criteria) this;
        }

        public Criteria andMessDtIdIn(List<Long> values) {
            addCriterion("mess_dt_id in", values, "messDtId");
            return (Criteria) this;
        }

        public Criteria andMessDtIdNotIn(List<Long> values) {
            addCriterion("mess_dt_id not in", values, "messDtId");
            return (Criteria) this;
        }

        public Criteria andMessDtIdBetween(Long value1, Long value2) {
            addCriterion("mess_dt_id between", value1, value2, "messDtId");
            return (Criteria) this;
        }

        public Criteria andMessDtIdNotBetween(Long value1, Long value2) {
            addCriterion("mess_dt_id not between", value1, value2, "messDtId");
            return (Criteria) this;
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

        public Criteria andTaxCodeIsNull() {
            addCriterion("tax_code is null");
            return (Criteria) this;
        }

        public Criteria andTaxCodeIsNotNull() {
            addCriterion("tax_code is not null");
            return (Criteria) this;
        }

        public Criteria andTaxCodeEqualTo(String value) {
            addCriterion("tax_code =", value, "taxCode");
            return (Criteria) this;
        }

        public Criteria andTaxCodeNotEqualTo(String value) {
            addCriterion("tax_code <>", value, "taxCode");
            return (Criteria) this;
        }

        public Criteria andTaxCodeGreaterThan(String value) {
            addCriterion("tax_code >", value, "taxCode");
            return (Criteria) this;
        }

        public Criteria andTaxCodeGreaterThanOrEqualTo(String value) {
            addCriterion("tax_code >=", value, "taxCode");
            return (Criteria) this;
        }

        public Criteria andTaxCodeLessThan(String value) {
            addCriterion("tax_code <", value, "taxCode");
            return (Criteria) this;
        }

        public Criteria andTaxCodeLessThanOrEqualTo(String value) {
            addCriterion("tax_code <=", value, "taxCode");
            return (Criteria) this;
        }

        public Criteria andTaxCodeLike(String value) {
            addCriterion("tax_code like", value, "taxCode");
            return (Criteria) this;
        }

        public Criteria andTaxCodeNotLike(String value) {
            addCriterion("tax_code not like", value, "taxCode");
            return (Criteria) this;
        }

        public Criteria andTaxCodeIn(List<String> values) {
            addCriterion("tax_code in", values, "taxCode");
            return (Criteria) this;
        }

        public Criteria andTaxCodeNotIn(List<String> values) {
            addCriterion("tax_code not in", values, "taxCode");
            return (Criteria) this;
        }

        public Criteria andTaxCodeBetween(String value1, String value2) {
            addCriterion("tax_code between", value1, value2, "taxCode");
            return (Criteria) this;
        }

        public Criteria andTaxCodeNotBetween(String value1, String value2) {
            addCriterion("tax_code not between", value1, value2, "taxCode");
            return (Criteria) this;
        }

        public Criteria andCorNameIsNull() {
            addCriterion("cor_name is null");
            return (Criteria) this;
        }

        public Criteria andCorNameIsNotNull() {
            addCriterion("cor_name is not null");
            return (Criteria) this;
        }

        public Criteria andCorNameEqualTo(String value) {
            addCriterion("cor_name =", value, "corName");
            return (Criteria) this;
        }

        public Criteria andCorNameNotEqualTo(String value) {
            addCriterion("cor_name <>", value, "corName");
            return (Criteria) this;
        }

        public Criteria andCorNameGreaterThan(String value) {
            addCriterion("cor_name >", value, "corName");
            return (Criteria) this;
        }

        public Criteria andCorNameGreaterThanOrEqualTo(String value) {
            addCriterion("cor_name >=", value, "corName");
            return (Criteria) this;
        }

        public Criteria andCorNameLessThan(String value) {
            addCriterion("cor_name <", value, "corName");
            return (Criteria) this;
        }

        public Criteria andCorNameLessThanOrEqualTo(String value) {
            addCriterion("cor_name <=", value, "corName");
            return (Criteria) this;
        }

        public Criteria andCorNameLike(String value) {
            addCriterion("cor_name like", value, "corName");
            return (Criteria) this;
        }

        public Criteria andCorNameNotLike(String value) {
            addCriterion("cor_name not like", value, "corName");
            return (Criteria) this;
        }

        public Criteria andCorNameIn(List<String> values) {
            addCriterion("cor_name in", values, "corName");
            return (Criteria) this;
        }

        public Criteria andCorNameNotIn(List<String> values) {
            addCriterion("cor_name not in", values, "corName");
            return (Criteria) this;
        }

        public Criteria andCorNameBetween(String value1, String value2) {
            addCriterion("cor_name between", value1, value2, "corName");
            return (Criteria) this;
        }

        public Criteria andCorNameNotBetween(String value1, String value2) {
            addCriterion("cor_name not between", value1, value2, "corName");
            return (Criteria) this;
        }

        public Criteria andCalledNumberIsNull() {
            addCriterion("called_number is null");
            return (Criteria) this;
        }

        public Criteria andCalledNumberIsNotNull() {
            addCriterion("called_number is not null");
            return (Criteria) this;
        }

        public Criteria andCalledNumberEqualTo(String value) {
            addCriterion("called_number =", value, "calledNumber");
            return (Criteria) this;
        }

        public Criteria andCalledNumberNotEqualTo(String value) {
            addCriterion("called_number <>", value, "calledNumber");
            return (Criteria) this;
        }

        public Criteria andCalledNumberGreaterThan(String value) {
            addCriterion("called_number >", value, "calledNumber");
            return (Criteria) this;
        }

        public Criteria andCalledNumberGreaterThanOrEqualTo(String value) {
            addCriterion("called_number >=", value, "calledNumber");
            return (Criteria) this;
        }

        public Criteria andCalledNumberLessThan(String value) {
            addCriterion("called_number <", value, "calledNumber");
            return (Criteria) this;
        }

        public Criteria andCalledNumberLessThanOrEqualTo(String value) {
            addCriterion("called_number <=", value, "calledNumber");
            return (Criteria) this;
        }

        public Criteria andCalledNumberLike(String value) {
            addCriterion("called_number like", value, "calledNumber");
            return (Criteria) this;
        }

        public Criteria andCalledNumberNotLike(String value) {
            addCriterion("called_number not like", value, "calledNumber");
            return (Criteria) this;
        }

        public Criteria andCalledNumberIn(List<String> values) {
            addCriterion("called_number in", values, "calledNumber");
            return (Criteria) this;
        }

        public Criteria andCalledNumberNotIn(List<String> values) {
            addCriterion("called_number not in", values, "calledNumber");
            return (Criteria) this;
        }

        public Criteria andCalledNumberBetween(String value1, String value2) {
            addCriterion("called_number between", value1, value2, "calledNumber");
            return (Criteria) this;
        }

        public Criteria andCalledNumberNotBetween(String value1, String value2) {
            addCriterion("called_number not between", value1, value2, "calledNumber");
            return (Criteria) this;
        }

        public Criteria andSmsContentIsNull() {
            addCriterion("sms_content is null");
            return (Criteria) this;
        }

        public Criteria andSmsContentIsNotNull() {
            addCriterion("sms_content is not null");
            return (Criteria) this;
        }

        public Criteria andSmsContentEqualTo(String value) {
            addCriterion("sms_content =", value, "smsContent");
            return (Criteria) this;
        }

        public Criteria andSmsContentNotEqualTo(String value) {
            addCriterion("sms_content <>", value, "smsContent");
            return (Criteria) this;
        }

        public Criteria andSmsContentGreaterThan(String value) {
            addCriterion("sms_content >", value, "smsContent");
            return (Criteria) this;
        }

        public Criteria andSmsContentGreaterThanOrEqualTo(String value) {
            addCriterion("sms_content >=", value, "smsContent");
            return (Criteria) this;
        }

        public Criteria andSmsContentLessThan(String value) {
            addCriterion("sms_content <", value, "smsContent");
            return (Criteria) this;
        }

        public Criteria andSmsContentLessThanOrEqualTo(String value) {
            addCriterion("sms_content <=", value, "smsContent");
            return (Criteria) this;
        }

        public Criteria andSmsContentLike(String value) {
            addCriterion("sms_content like", value, "smsContent");
            return (Criteria) this;
        }

        public Criteria andSmsContentNotLike(String value) {
            addCriterion("sms_content not like", value, "smsContent");
            return (Criteria) this;
        }

        public Criteria andSmsContentIn(List<String> values) {
            addCriterion("sms_content in", values, "smsContent");
            return (Criteria) this;
        }

        public Criteria andSmsContentNotIn(List<String> values) {
            addCriterion("sms_content not in", values, "smsContent");
            return (Criteria) this;
        }

        public Criteria andSmsContentBetween(String value1, String value2) {
            addCriterion("sms_content between", value1, value2, "smsContent");
            return (Criteria) this;
        }

        public Criteria andSmsContentNotBetween(String value1, String value2) {
            addCriterion("sms_content not between", value1, value2, "smsContent");
            return (Criteria) this;
        }

        public Criteria andStatusIsNull() {
            addCriterion("status is null");
            return (Criteria) this;
        }

        public Criteria andStatusIsNotNull() {
            addCriterion("status is not null");
            return (Criteria) this;
        }

        public Criteria andStatusEqualTo(Byte value) {
            addCriterion("status =", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotEqualTo(Byte value) {
            addCriterion("status <>", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThan(Byte value) {
            addCriterion("status >", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThanOrEqualTo(Byte value) {
            addCriterion("status >=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThan(Byte value) {
            addCriterion("status <", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThanOrEqualTo(Byte value) {
            addCriterion("status <=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusIn(List<Byte> values) {
            addCriterion("status in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotIn(List<Byte> values) {
            addCriterion("status not in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusBetween(Byte value1, Byte value2) {
            addCriterion("status between", value1, value2, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotBetween(Byte value1, Byte value2) {
            addCriterion("status not between", value1, value2, "status");
            return (Criteria) this;
        }

        public Criteria andSendResultIsNull() {
            addCriterion("send_result is null");
            return (Criteria) this;
        }

        public Criteria andSendResultIsNotNull() {
            addCriterion("send_result is not null");
            return (Criteria) this;
        }

        public Criteria andSendResultEqualTo(String value) {
            addCriterion("send_result =", value, "sendResult");
            return (Criteria) this;
        }

        public Criteria andSendResultNotEqualTo(String value) {
            addCriterion("send_result <>", value, "sendResult");
            return (Criteria) this;
        }

        public Criteria andSendResultGreaterThan(String value) {
            addCriterion("send_result >", value, "sendResult");
            return (Criteria) this;
        }

        public Criteria andSendResultGreaterThanOrEqualTo(String value) {
            addCriterion("send_result >=", value, "sendResult");
            return (Criteria) this;
        }

        public Criteria andSendResultLessThan(String value) {
            addCriterion("send_result <", value, "sendResult");
            return (Criteria) this;
        }

        public Criteria andSendResultLessThanOrEqualTo(String value) {
            addCriterion("send_result <=", value, "sendResult");
            return (Criteria) this;
        }

        public Criteria andSendResultLike(String value) {
            addCriterion("send_result like", value, "sendResult");
            return (Criteria) this;
        }

        public Criteria andSendResultNotLike(String value) {
            addCriterion("send_result not like", value, "sendResult");
            return (Criteria) this;
        }

        public Criteria andSendResultIn(List<String> values) {
            addCriterion("send_result in", values, "sendResult");
            return (Criteria) this;
        }

        public Criteria andSendResultNotIn(List<String> values) {
            addCriterion("send_result not in", values, "sendResult");
            return (Criteria) this;
        }

        public Criteria andSendResultBetween(String value1, String value2) {
            addCriterion("send_result between", value1, value2, "sendResult");
            return (Criteria) this;
        }

        public Criteria andSendResultNotBetween(String value1, String value2) {
            addCriterion("send_result not between", value1, value2, "sendResult");
            return (Criteria) this;
        }

        public Criteria andTaxCodeLikeInsensitive(String value) {
            addCriterion("upper(tax_code) like", value.toUpperCase(), "taxCode");
            return (Criteria) this;
        }

        public Criteria andCorNameLikeInsensitive(String value) {
            addCriterion("upper(cor_name) like", value.toUpperCase(), "corName");
            return (Criteria) this;
        }

        public Criteria andCalledNumberLikeInsensitive(String value) {
            addCriterion("upper(called_number) like", value.toUpperCase(), "calledNumber");
            return (Criteria) this;
        }

        public Criteria andSmsContentLikeInsensitive(String value) {
            addCriterion("upper(sms_content) like", value.toUpperCase(), "smsContent");
            return (Criteria) this;
        }

        public Criteria andSendResultLikeInsensitive(String value) {
            addCriterion("upper(send_result) like", value.toUpperCase(), "sendResult");
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