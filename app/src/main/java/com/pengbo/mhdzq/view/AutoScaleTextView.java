package com.pengbo.mhdzq.view;

import com.pengbo.mhdzq.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

/**
 * A custom Text View that lowers the text size when the text is to big for the TextView. Modified version of one found on stackoverflow
 * @version 1.0
 * 
 */
public class AutoScaleTextView extends TextView
{
	private Paint textPaint;

	private float preferredTextSize;
	private float minTextSize;

	public AutoScaleTextView(Context context)
	{
		this(context, null);
	}

	public AutoScaleTextView(Context context, AttributeSet attrs)
	{
		this(context, attrs, R.attr.autoScaleTextViewStyle);

		// Use this constructor, if you do not want use the default style
		// super(context, attrs);
	}

	public AutoScaleTextView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);

		this.textPaint = new Paint();

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AutoScaleTextView, defStyle, 0);
		this.minTextSize = a.getDimension(R.styleable.AutoScaleTextView_minTextSize, 10f);
		a.recycle();

		this.preferredTextSize = this.getTextSize();
	}

	/**
	 * Set the minimum text size for this view
	 * 
	 * @param minTextSize
	 *            The minimum text size
	 */
	public void setMinTextSize(float minTextSize)
	{
		this.minTextSize = minTextSize;
	}

	/**
	 * Resize the text so that it fits
	 * 
	 * @param text
	 *            The text. Neither <code>null</code> nor empty.
	 * @param textWidth
	 *            The width of the TextView. > 0
	 */
	private void refitText(String text, int textWidth)
	{
		if (textWidth <= 0 || text == null || text.length() == 0)
			return;

		// the width
		int targetWidth = textWidth - this.getPaddingLeft() - this.getPaddingRight();

		final float threshold = 0.5f; // How close we have to be

		this.textPaint.set(this.getPaint());
		float preferSize = preferredTextSize;
		float minSize = minTextSize;

		while ((preferSize - minSize) > threshold)
		{
			float size = (preferSize + minSize) / 2;
			this.textPaint.setTextSize(size);
			if (this.textPaint.measureText(text) >= targetWidth)
				preferSize = size; // too big
			else
				minSize = size; // too small
		}
		// Use min size so that we undershoot rather than overshoot
		this.setTextSize(TypedValue.COMPLEX_UNIT_PX, minSize);
	}

	@Override
	protected void onTextChanged(CharSequence text, int start, int before, int after)
	{
		super.onTextChanged(text, start, before, after);
		this.refitText(text.toString(), this.getWidth());
	}

	@Override
	protected void onSizeChanged(int width, int height, int oldwidth, int oldheight)
	{
		if (width != oldwidth)
			this.refitText(this.getText().toString(), width);
	}

}