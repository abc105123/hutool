/*
 * Copyright (c) 2013-2025 Hutool Team and hutool.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dromara.hutool.swing.captcha;

import org.dromara.hutool.core.util.ObjUtil;
import org.dromara.hutool.core.util.RandomUtil;
import org.dromara.hutool.swing.captcha.generator.CodeGenerator;
import org.dromara.hutool.swing.captcha.generator.RandomGenerator;
import org.dromara.hutool.swing.img.color.ColorUtil;
import org.dromara.hutool.swing.img.GraphicsUtil;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

/**
 * 扭曲干扰验证码
 *
 * @author Looly
 * @since 3.2.3
 */
public class ShearCaptcha extends AbstractCaptcha {
	private static final long serialVersionUID = -7096627300356535494L;

	/**
	 * 构造
	 *
	 * @param width  图片宽
	 * @param height 图片高
	 */
	public ShearCaptcha(final int width, final int height) {
		this(width, height, 5);
	}

	/**
	 * 构造
	 *
	 * @param width     图片宽
	 * @param height    图片高
	 * @param codeCount 字符个数
	 */
	public ShearCaptcha(final int width, final int height, final int codeCount) {
		this(width, height, codeCount, 4);
	}

	/**
	 * 构造
	 *
	 * @param width     图片宽
	 * @param height    图片高
	 * @param codeCount 字符个数
	 * @param thickness 干扰线宽度
	 */
	public ShearCaptcha(final int width, final int height, final int codeCount, final int thickness) {
		this(width, height, new RandomGenerator(codeCount), thickness);
	}

	/**
	 * 构造
	 *
	 * @param width          图片宽
	 * @param height         图片高
	 * @param generator      验证码生成器
	 * @param interfereCount 验证码干扰元素个数
	 */
	public ShearCaptcha(final int width, final int height, final CodeGenerator generator, final int interfereCount) {
		super(width, height, generator, interfereCount);
	}

	/**
	 * 构造
	 *
	 * @param width          图片宽
	 * @param height         图片高
	 * @param codeCount      字符个数
	 * @param interfereCount 验证码干扰元素个数
	 * @param sizeBaseHeight 字体的大小 高度的倍数
	 */
	public ShearCaptcha(final int width, final int height, final int codeCount, final int interfereCount, final float sizeBaseHeight) {
		super(width, height, new RandomGenerator(codeCount), interfereCount, sizeBaseHeight);
	}

	@Override
	public Image createImage(final String code) {
		final BufferedImage image = new BufferedImage(width, height, (null == this.background) ? BufferedImage.TYPE_4BYTE_ABGR : BufferedImage.TYPE_INT_RGB);
		final Graphics2D g = GraphicsUtil.createGraphics(image, this.background);

		try {
			// 画字符串
			drawString(g, code);
			// 扭曲
			shear(g, this.width, this.height, ObjUtil.defaultIfNull(this.background, Color.WHITE));
			// 画干扰线
			drawInterfere(g, 0, RandomUtil.randomInt(this.height) + 1, this.width, RandomUtil.randomInt(this.height) + 1, this.interfereCount, ColorUtil.randomColor());
		} finally {
			g.dispose();
		}

		return image;
	}

	// ----------------------------------------------------------------------------------------------------- Private method start

	/**
	 * 绘制字符串
	 *
	 * @param g    {@link Graphics}画笔
	 * @param code 验证码
	 */
	private void drawString(final Graphics2D g, final String code) {
		// 指定透明度
		if (null != this.textAlpha) {
			g.setComposite(this.textAlpha);
		}
		GraphicsUtil.drawStringColourful(g, code, this.font, this.width, this.height);
	}

	/**
	 * 扭曲
	 *
	 * @param g     {@link Graphics}
	 * @param w1    w1
	 * @param h1    h1
	 * @param color 颜色
	 */
	private void shear(final Graphics g, final int w1, final int h1, final Color color) {
		shearX(g, w1, h1, color);
		shearY(g, w1, h1, color);
	}

	/**
	 * X坐标扭曲
	 *
	 * @param g     {@link Graphics}
	 * @param w1    宽
	 * @param h1    高
	 * @param color 颜色
	 */
	private void shearX(final Graphics g, final int w1, final int h1, final Color color) {

		final int period = RandomUtil.randomInt(this.width);

		final int frames = 1;
		final int phase = RandomUtil.randomInt(2);

		for (int i = 0; i < h1; i++) {
			final double d = (double) (period >> 1) * Math.sin((double) i / (double) period + (6.2831853071795862D * (double) phase) / (double) frames);
			g.copyArea(0, i, w1, 1, (int) d, 0);
			g.setColor(color);
			g.drawLine((int) d, i, 0, i);
			g.drawLine((int) d + w1, i, w1, i);
		}

	}

	/**
	 * Y坐标扭曲
	 *
	 * @param g     {@link Graphics}
	 * @param w1    宽
	 * @param h1    高
	 * @param color 颜色
	 */
	private void shearY(final Graphics g, final int w1, final int h1, final Color color) {

		final int period = RandomUtil.randomInt(this.height >> 1);

		final int frames = 20;
		final int phase = 7;
		for (int i = 0; i < w1; i++) {
			final double d = (double) (period >> 1) * Math.sin((double) i / (double) period + (6.2831853071795862D * (double) phase) / (double) frames);
			g.copyArea(i, 0, 1, h1, 0, (int) d);
			g.setColor(color);
			// 擦除原位置的痕迹
			g.drawLine(i, (int) d, i, 0);
			g.drawLine(i, (int) d + h1, i, h1);
		}

	}

	/**
	 * 干扰线
	 *
	 * @param g         {@link Graphics}
	 * @param x1        x1
	 * @param y1        y1
	 * @param x2        x2
	 * @param y2        y2
	 * @param thickness 粗细
	 * @param c         颜色
	 */
	@SuppressWarnings("SameParameterValue")
	private void drawInterfere(final Graphics g, final int x1, final int y1, final int x2, final int y2, final int thickness, final Color c) {

		// The thick line is in fact a filled polygon
		g.setColor(c);
		final int dX = x2 - x1;
		final int dY = y2 - y1;
		// line length
		final double lineLength = Math.sqrt(dX * dX + dY * dY);

		final double scale = (double) (thickness) / (2 * lineLength);

		// The x and y increments from an endpoint needed to create a
		// rectangle...
		double ddx = -scale * (double) dY;
		double ddy = scale * (double) dX;
		ddx += (ddx > 0) ? 0.5 : -0.5;
		ddy += (ddy > 0) ? 0.5 : -0.5;
		final int dx = (int) ddx;
		final int dy = (int) ddy;

		// Now we can compute the corner points...
		final int[] xPoints = new int[4];
		final int[] yPoints = new int[4];

		xPoints[0] = x1 + dx;
		yPoints[0] = y1 + dy;
		xPoints[1] = x1 - dx;
		yPoints[1] = y1 - dy;
		xPoints[2] = x2 - dx;
		yPoints[2] = y2 - dy;
		xPoints[3] = x2 + dx;
		yPoints[3] = y2 + dy;

		g.fillPolygon(xPoints, yPoints, 4);
	}
	// ----------------------------------------------------------------------------------------------------- Private method end
}
