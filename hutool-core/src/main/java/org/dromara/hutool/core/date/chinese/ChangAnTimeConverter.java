package org.dromara.hutool.core.date.chinese;

import org.dromara.hutool.core.date.DateBetween;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 长安时辰转换器
 * <p>
 * 	   23-1 -> 子时
 *     1-3 -> 丑时
 *     3-5 -> 寅时
 *     5-7 -> 卯时
 *     7-9 -> 辰时
 *     9-11 -> 巳时
 *     11-13 -> 午时
 *     13-15 -> 未时
 *     15-17 -> 申时
 *     17-19 -> 酉时
 *     19-21 -> 戌时
 *     21-23 -> 亥时
 *     24/-1/其他值 -> 未知
 *     </p>
 * @author achao@hutool.cn
 */
public class ChangAnTimeConverter {

	private static final Map<String, int[]> timeMap = new HashMap<>();

	static {
		// 初始化时辰对应的小时范围
		timeMap.put("子", new int[]{23, 1});
		timeMap.put("丑", new int[]{1, 3});
		timeMap.put("寅", new int[]{3, 5});
		timeMap.put("卯", new int[]{5, 7});
		timeMap.put("辰", new int[]{7, 9});
		timeMap.put("巳", new int[]{9, 11});
		timeMap.put("午", new int[]{11, 13});
		timeMap.put("未", new int[]{13, 15});
		timeMap.put("申", new int[]{15, 17});
		timeMap.put("酉", new int[]{17, 19});
		timeMap.put("戌", new int[]{19, 21});
		timeMap.put("亥", new int[]{21, 23});
	}

	/**
	 * 将长安时辰转换为现代时间
	 * <p>
	 * toModernTime("子时").getBegin().getHours() -> 23
	 * toModernTime("子时").getEnd().getHours() -> 1
	 * </p>
	 *
	 * @param changAnTime 长安时辰
	 * @return 现代时间段
	 */
	public static DateBetween toModernTime(String changAnTime) {
		String time = changAnTime.replace("时", "");
		int[] hours = timeMap.get(time);
		if (hours == null) {
			throw new IllegalArgumentException("Invalid ChangAn time");
		}

		LocalDateTime now = LocalDateTime.now();
		LocalDateTime start = now.withHour(hours[0]).withMinute(0).withSecond(0).withNano(0);
		LocalDateTime end = now.withHour(hours[1]).withMinute(0).withSecond(0).withNano(0);
		if (hours[0] >= hours[1]) {
			end = end.plusDays(1); // 处理跨日情况
		}

		Date startDate = Date.from(start.atZone(ZoneId.systemDefault()).toInstant());
		Date endDate = Date.from(end.atZone(ZoneId.systemDefault()).toInstant());

		return DateBetween.of(startDate, endDate);
	}

	/**
	 * 将小时转换为长安时辰
	 * <p>
	 *     toChangAnTime(1) -> "子时"
	 *</p>
	 * @param hour 小时
	 * @return 长安时辰
	 */
	public static String toChangAnTime(int hour) {
		for (Map.Entry<String, int[]> entry : timeMap.entrySet()) {
			int startHour = entry.getValue()[0];
			int endHour = entry.getValue()[1];
			if (hour == 23 || hour == 0 || (hour >= startHour && hour < endHour) || (startHour > endHour && hour < endHour)) {
				return entry.getKey() + "时";
			}
		}
		return "未知";
	}

}
