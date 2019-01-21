package com.rongf.ticket.utils;

public class PublicInfo {
	public final static String QUERY_TICKET_URL = "https://kyfw.12306.cn/otn/leftTicket/queryZ";
	public final static String QUERY_TRAIN_NO_URL = "https://kyfw.12306.cn/otn/czxx/queryByTrainNo";
	public final static String GET_RAND_URL = "https://kyfw.12306.cn/passport/captcha/captcha-image64?login_site=E&module=login&rand=sjrand&";
	public final static String RAND_CHECK_URL = "https://kyfw.12306.cn/passport/captcha/captcha-check";
	public final static String LOGIN_URL = "https://kyfw.12306.cn/passport/web/login";
	public final static String ASYLOGIN_URL = "https://kyfw.12306.cn/otn/login/loginAysnSuggest";
	public final static String UAMTK_URL = "https://kyfw.12306.cn/passport/web/auth/uamtk";
	public final static String UAMAUTHCLIENT_URL = "https://kyfw.12306.cn/otn/uamauthclient";
	public final static String USER_LOGIN_URL = "https://kyfw.12306.cn/otn/login/userLogin";
	public final static String INDEX_URL = "https://kyfw.12306.cn/otn/view/index.html";
	public final static String CONF_URL = "https://kyfw.12306.cn/otn/login/conf";
	public final static String INIT_API_URL = "https://kyfw.12306.cn/otn/index/initMy12306Api";
	public final static String CHECK_USER_URL = "https://kyfw.12306.cn/otn/login/checkUser";
	public final static String BEFORE_SUBMIT_URL = "https://kyfw.12306.cn/otn/leftTicket/submitOrderRequest";
	public final static String ORDER_INIT_URL = "https://kyfw.12306.cn/otn/confirmPassenger/initDc";
	public final static String GET_PASSENGER_URL = "https://kyfw.12306.cn/otn/confirmPassenger/getPassengerDTOs";
	public final static String CHECK_ORDER_URL = "https://kyfw.12306.cn/otn/confirmPassenger/checkOrderInfo";
	public final static String GET_QUEUE_COUNT_URL = "https://kyfw.12306.cn/otn/confirmPassenger/getQueueCount";
	public final static String CONFIRM_FOR_QUEUE = "https://kyfw.12306.cn/otn/confirmPassenger/confirmSingleForQueue";
	public final static String WAIT_ORDER_URL = "https://kyfw.12306.cn/otn/confirmPassenger/queryOrderWaitTime?random=";
	
	public enum TrainType {
		// 高铁
		G("G"),
		// 城际
		C("C"),
		// 动车
		D("D"),
		// 直达
		Z("Z"),
		// 特快
		T("T"),
		// 快车
		K("K"),
		// 临时
		L("L");
		
		private String trainType;
		
		private TrainType(String trainType) {
			this.trainType = trainType;
		}
		
		public String getTrainType() {
			return this.trainType;
		}
	}
	
	public enum SeatType {
		// TODO
		GG("gg", ""),
		// 高级软卧
		GR("gr", ""),
		// 其他
		QT("qt", ""),
		// 软卧
		RW("rw", "4"),
		// 软座
		RZ("rz", "2"),
		// TODO
		TZ("tz", ""),
		// TODO 硬卧代硬座
		WZ("wz", ""),
		// TODO
		YB("yb", ""),
		// 硬卧
		YW("yw", "3"),
		// 硬座
		YZ("yz", "1"),
		// 二等座
		ZE("ze", "O"),
		// 一等座
		ZY("zy", "M"),
		// 商务座
		SWZ("swz", ""),
		// TODO
		SRRB("srrb", "");
		
		private String seatType;
		private String seatTypeCode;
		
		private SeatType(String seatType, String seatTypeCode) {
			this.seatType = seatType;
			this.seatTypeCode = seatTypeCode;
		}
		
		public String getSeatType() {
			return this.seatType;
		}
		
		public String getSeatTypeCode() {
			return this.seatTypeCode;
		}

		public static SeatType getSeatForName(String seatTypeName) {
			for (SeatType seatType : SeatType.values())
				if (seatType.getSeatType().equals(seatTypeName.toLowerCase()))
					return seatType;
			return null;
		}
	}
}
