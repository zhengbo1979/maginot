package com.sample.esper;

/**
 * @author zhengbo
 * @date 2016��10��9��
 * 
 */

public class EsperView {
	private static String PRODUCT = Apple.class.getName();
    /**
     * Table 12.1. Built-in Data Window Views
     * <p>
     * View	Syntax	                                             Description
     * ==========================================================================================================
     * win:length(size)	                                �������ڣ�������� size ������
     * win:length_batch(size)	                        ������-������ڣ����� size ��������ѯ
     * win:time(time period)	                        �������ڣ������ size ���ڵ�����
     * win:ext_timed(timestamp expression, time period)	�������ڣ������ time period �ڵ����ݣ����ٻ���EPL���棬����ϵͳʱ���[timestamp]
     * win:time_batch(time period[,optional reference point] [, flow control])	            �����¼����ͷ�����ָ����ʱ����,��������ѡ��
     * win:ext_timed_batch(timestamp expression, time period[,optional reference point])	�������¼����ͷ����ǵĻ������ɱ��ʽ�ṩ�ĺ���ֵÿ��ָ����ʱ����
     * win:time_length_batch(time period, size [, flow control])	����Ե�ʱ��ͳ��ȵ���������,��ʱ�����������һ����������ʱ��������ѯ
     * win:time_accum(time period)	                                ���������ֱ�� period ��û���½��������ʱ���������������ѯ. ��Щ������Ϊ���Ƴ�����
     * win:keepall()	                                         keep-all �޲�������¼���н�������ݣ�����ʹ��delete���������ܴӴ����Ƴ�����
     * ext:sort(size, sort criteria)	                            ����������׼���ʽ���ص�ֵ,�������¼��������Ĵ�С�� Q:select sum(amount) from userbuy.ext:sort(3, amount desc) ��3����߽�����
     * ext:rank(unique criteria(s), size, sort criteria(s))	        ֻ����������¼�������ͬ��ֵΪ��׼���ʽ(s)��ĳ�ֱ�׼���ʽ�ͽ����������¼��������Ĵ�С�� Q:select * from userbuy.ext:sort(3,amount desc,id asc) �ҳ���߽���3���¼�������id����
     * ext:time_order(timestamp expression, time period)	        Orders events that arrive out-of-order, using an expression providing timestamps to be ordered.
     * std:unique(unique criteria(s))	        �Բ�ͬ��unique[����:id]�����������һ���¼�
     * std:groupwin(grouping criteria(s))	    һ���������������ʹ�ã�����������ݰ����ʽ id ����
     * ���ڷ����ʱ�䴰��:��ʹ��grouped-windowʱ�䴰��,ע��,�Ƿ���5���ӵ��¼���ÿ�鱣��5���ӵ��¼�,
     * �������һ���Ĵӱ����ĽǶ��¼��ȱ�������,����������,ͬһ����¼�������뵥��ָ��ʱ�䴰
     * std:lastevent()	                        �������һ���¼�������Ϊ1
     * std:firstevent()	                        ������һ���¼������˺������¼�
     * std:firstunique(unique criteria(s))	    Retains only the very first among events having the same value for the criteria expression(s), disregarding all subsequent events for same value(s).
     * win:firstlength(size)	                ����һ�����ݵ�ǰ size ���������delete����
     * win:firsttime(time period)	            �������ڳ�ʼ���� period �ڵ���������
     *
     * @return epl
     */
    protected static String dataWindowViews() {
        //ÿ����3���¼���ͳ�����һ��newEvents[3]
        String epl1 = "select price from " + PRODUCT + ".win:length_batch(3)";
        //ÿ����3���¼��󣬰�price����ͳ��3���¼���ÿ�� price ���ֵĴ���
        // TODO Ĭ�ϼ�¼����ǰ�ķ���ͳ����Ϣ����ǰ10���¼��в�������ǰ����ͳ����Ϣ���� count(price)Ϊ0
        String epl2 = "select rstream price , count(price) from " + PRODUCT + ".win:length_batch(3) group by price";
        //[istream | rstream]�Ĺ��������ǲ���һ�����ݺ�istream��3���������봰�ڵ����ݣ�3�����rstream�����ͬ��������
        String epl3 = "select rstream price , count(price) from " + PRODUCT + ".win:time(5 seconds) group by price";
        // "ERROR-> Caused by: com.espertech.esper.view.ViewParameterException: Invalid parameter expression 0: Property named 'timestamp' is not valid in any stream"
        String epl4 = "select price from " + PRODUCT + ".win:ext_timed(timestamp, 10 sec)";
        //win:time_batch(10 sec,"FORCE_UPDATE, START_EAGER") ���������������󣬻��ڴ��ڳ�ʼ��ʱ��ִ�в�ѯ��������û�����ݽ�����Ƴ�ʱ��ǿ�Ʋ�ѯ�����
        String epl5 = "select * from " + PRODUCT + ".win:time_batch(10 sec, \"FORCE_UPDATE, START_EAGER\")";
        // ʱ���������������һ�������
        String epl6 = "select price from " + PRODUCT + ".win:time_length_batch(3 sec, 5, \"FORCE_UPDATE, START_EAGER\")";
        //3s ��û�����ݽ����������������Ƴ�����
        String epl7 = " select rstream price from " + PRODUCT + ".win:time_accum(3 sec)";
        //ֻ���ǰ10���¼�
        String epl8 = "select price from " + PRODUCT + ".win:firstlength(10)";
        //�� id �� price ���飬�������һ���¼���¼ ��ͬ��std:groupwin(id, price).win:length(1)
        String epl9 = "select price from " + PRODUCT + ".std:unique(id, price)";
        //��������ݰ�id���飬��ͬid��������Ŀ��������3 "����������ȷ"
        String epl10 = "select sum(price) from " + PRODUCT + ".std:groupwin(id).win:length(3)";
        //ͳ��ÿ��id���3�����ѵ���price
        String epl11 = "select sum(price) from " + PRODUCT + ".std:groupwin(id).win:length(3) group by id";
        //��ǰ������ ����3�� price ��sum ֵ
        String epl12 = "select sum(price) from " + PRODUCT + ".ext:sort(3, price desc)";
        //��ǰ������ �� price ���� id ���� ��price sum ֵ
        String epl13 = "select sum(price) from " + PRODUCT + ".ext:sort(3, price desc, id asc)";
        //�� size ���飬ֻ�������һ������� size ���ݣ�ȡǰ3�� price �� sum ֵ
        String epl14 = "select sum(price) from " + PRODUCT + ".ext:rank(size, 3, price desc)";
        //create_time ʱ�����򣬱���4s ������
        String epl15 = "select rstream * from " + PRODUCT + ".ext:time_order(create_time, 4 sec)";
        return epl15;
    }

    /**
     * win:expr(expiry expression)	    ���ڵ��¼��Ƿ���ȡ���ڹ��ڱ��ʽexpression�Ľ����
     * expression�൱�ڿ��أ����һֱ��true����ôʱ�����view���Ƴ���ֱ��expressionΪfalse����֮ǰ�������¼���view��ȫ��ɾ��
     * <p>
     * Table 12.3. Built-in Properties of the Expiry Expression Data Window View
     * <p>
     * Name	            Type	                        Description
     * ================================================================================
     * current_count	int	            ��ǰ���ݴ��ڰ���������¼�������
     * expired_count	int	            �������ڵ��¼�����
     * newest_event	    (same event type as arriving events)	���һ��������¼�
     * newest_timestamp	long	        �����ʱ�����last-arriving�¼��йء�
     * oldest_event	    (same event type as arriving events)	currently-evaluated�¼�����
     * oldest_timestamp	long            �����ʱ�����currently-evaluated�¼��йء�
     * view_reference	Object	        ��ǰ��ͼ����Ķ���
     *
     * @return epl
     */
    protected static String expr() {
        //�����������2���¼�
        String epl1 = "select rstream price from " + PRODUCT + ".win:expr(current_count <= 2)";
        //�ѹ����¼�����
        String epl2 = "select rstream price from " + PRODUCT + ".win:expr(expired_count = 2)";
        //�������5�������¼�
        String epl3 = "select rstream price from " + PRODUCT + ".win:expr(oldest_timestamp > newest_timestamp - 5000)";
        // id ��ͬ��־���¼������������ id ֵ�仯���Ƴ��������¼�
        String epl4 = "select rstream price from " + PRODUCT + ".win:expr(newest_event.id = oldest_event.id)";
        return epl2;
    }

    /**
     * win:expr_batch(expiry expression) �¼�����,���δ������ڹ��ڱ��ʽ�Ľ����Ϊһ�����������ж��Ƿ��Ƴ�����
     * �ο�{@link #expr()}
     * <p>
     * Table 12.4. Built-in Properties of the Expiry Expression Data Window View
     * <p>
     * Name	            Type	                        Description
     * ================================================================================
     * current_count	int	            �����ݴ��ڰ�����ǰ������¼����¼�������
     * newest_event	    (same event type as arriving events)	���һ��������¼�
     * newest_timestamp	long	        �����ʱ�����last-arriving�¼��йء�
     * oldest_event	    (same event type as arriving events)	currently-evaluated�¼�����
     * oldest_timestamp	long            �����ʱ�����currently-evaluated�¼��йء�
     * view_reference	Object	        ��ǰ��ͼ����Ķ���
     *
     * @return epl
     */
    protected static String expr_batch() {
        //��current_count�������¼��������飬�����������
        String epl1 = "select rstream price from " + PRODUCT + ".win:expr_batch(current_count > 2)";
        //�������5�������¼�����- 5���ڵ��¼����������
        String epl2 = "select rstream price from " + PRODUCT + ".win:expr_batch(oldest_timestamp > newest_timestamp - 5000)";
        return epl1;
    }

    /**
     * ����Ϊ���ݹ�ʽ����
     */

    /**
     * View	                    Syntax	                                                                Description
     * =============================================================================================================================
     * Size	                    std:size([expression, ...])	                                            Derives a count of the number of events in a data window, or in an insert stream if used without a data window, and optionally provides additional event properties as listed in parameters.
     *
     * @return epl
     */
    protected static String size_Views() {
        //ͳ�ư�price���� �¼�����
        String epl1 = "select size from " + PRODUCT + ".std:groupwin(price).std:size()";
        String epl2 = "select  size ,id ,price from " + PRODUCT + ".win:time(3 sec).std:size(id, price)";
        String epl3 = "select size from " + PRODUCT + ".win:time(3 sec).std:size()";
        return epl3;
    }

    /**
     * stat:uni����
     * <p>
     * View	                    Syntax	                                 Description
     * ============================================================================================================
     * ������ͳ������    stat:uni(value expression [,expression, ...])	     ͳ���ɵ�����ͳ�Ƴ���ֵ
     * <p>
     * <p>
     * Property Name	Description
     * =========================================================
     * datapoints	ֵ������, �൱��  count(*)
     * total	    �ܼ�  ,  �൱��sum()
     * average	    ƽ��ֵ
     * variance	    ����
     * stddev	    �����ı�׼ƫ��(�����ƽ����)
     * stddevpa	    �����׼ƫ��
     *
     * @return epl
     */
    protected static String stat_uni_Views() {
        String epl1 = "select stddev from " + PRODUCT + ".win:length(3).stat:uni(price)";
        String epl2 = "select total from " + PRODUCT + ".win:length(3).stat:uni(price)";
        String epl3 = "select average from " + PRODUCT + ".win:length(3).stat:uni(price)";
        String epl4 = "select datapoints from " + PRODUCT + ".win:length(3).stat:uni(price)";
        return epl3;
    }

    /**
     * stat:linest  �����������ʽ�ķ���ֵ�Ļع����ص��м�����
     * <p>
     * View	                    Syntax	                                 Description
     * ============================================================================================================
     * Regression	            stat:linest(value expression, value expression [,expression, ...])	    Calculates regression on the values returned by two expressions.
     * <p>
     * �ο��ĵ� 12.4.2. Regression (stat:linest) ����
     *
     * @return epl
     */
    protected static String stat_linest_Views() {
        //�����ʾ��ѡ�����е�����ֵ���������¼����ԣ�
        String epl3 = "select * from " + PRODUCT + ".win:time(10 seconds).stat:linest(price, offer, *)";
        return epl3;
    }

    /**
     * stat:correl ���������¼��������
     * <p>
     * View	                    Syntax	                                                                Description
     * ============================================================================================================
     * Correlation	            stat:correl(value expression, value expression [,expression, ...])	    ����2�����ʽ���ص�ֵ�����ֵ
     * <p>
     * Property Name	Description
     * =========================================================
     * correlation	    �����¼��������
     *
     * @return epl
     */
    protected static String stat_correl_Views() {
        //����۸�������
        String epl1 = "select correlation from " + PRODUCT + ".stat:correl(size,price)";
        String epl2 = "select * from " + PRODUCT + ".stat:correl(price, size, *)";
        return epl2;
    }


    /**
     * stat:weighted_avg ��Ȩƽ����
     * <p>
     * View	                    Syntax	                                                                 Description
     * ============================================================================================================
     * Weighted average	        stat:weighted_avg(value expression, value expression [,expression, ...]) ���ظ�����ֵ�������ƽ��ֵ�ͱ��ʽ�����������ʽ�ļ�Ȩƽ��ֵ
     * <p>
     * Property Name	Description
     * =========================================================
     * average	        ��Ȩƽ����
     *
     * @return epl
     */
    protected static String stat_weighted_avg_Views() {
        //����۸�������
        String epl1 = "select average " +
                "from " + PRODUCT + ".win:time(3 seconds).stat:weighted_avg(price, size)";
        return epl1;
    }
}