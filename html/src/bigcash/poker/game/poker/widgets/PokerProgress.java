package bigcash.poker.game.poker.widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.scenes.scene2d.utils.Disableable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Pools;

public class PokerProgress extends Widget implements Disableable {
    private ProgressBarStyle style;
    private float min, max, stepSize;
    private float value, animateFromValue;
    float position;
    final boolean vertical;
    private float animateDuration, animateTime;
    private Interpolation animateInterpolation = Interpolation.linear;
    boolean disabled;
    private Interpolation visualInterpolation = Interpolation.linear;
    private boolean round = true;
    public PokerProgress(float min, float max, float stepSize, boolean vertical, ProgressBarStyle style) {
        if (min > max)
            throw new IllegalArgumentException("max must be > min. min,max: " + min + ", " + max);
        if (stepSize <= 0) throw new IllegalArgumentException("stepSize must be > 0: " + stepSize);
        setStyle(style);
        this.min = min;
        this.max = max;
        this.stepSize = stepSize;
        this.vertical = vertical;
        this.value = min;
        setSize(getPrefWidth(), getPrefHeight());
    }

    public void setStyle(ProgressBarStyle style) {
        if (style == null) throw new IllegalArgumentException("style cannot be null.");
        this.style = style;
        invalidateHierarchy();
    }

    public ProgressBarStyle getStyle() {
        return style;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (animateTime > 0) {
            animateTime -= delta;
            Stage stage = getStage();
            if (stage != null && stage.getActionsRequestRendering())
                Gdx.graphics.requestRendering();
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
       ProgressBarStyle style = this.style;
        boolean disabled = this.disabled;
        final Drawable knob = getKnobDrawable();
        final Drawable bg = (disabled && style.disabledBackground != null) ? style.disabledBackground : style.background;
        final Drawable knobBefore = (disabled && style.disabledKnobBefore != null) ? style.disabledKnobBefore : style.knobBefore;
        final Drawable knobAfter = (disabled && style.disabledKnobAfter != null) ? style.disabledKnobAfter : style.knobAfter;

        Color color = getColor();
        float x = getX();
        float y = getY();
        float width = getWidth();
        float height = getHeight();
        float knobHeight = knob == null ? 0 : knob.getMinHeight();
        float knobWidth = knob == null ? 0 : knob.getMinWidth();
        float percent = getVisualPercent();

        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);

        if (vertical) {
            float positionHeight = height;

            float bgTopHeight = 0, bgBottomHeight = 0;
            if (bg != null) {
                if (round)
                    bg.draw(batch, Math.round(x + (width - bg.getMinWidth()) * 0.5f), y, Math.round(bg.getMinWidth()), height);
                else
                    bg.draw(batch, x + width - bg.getMinWidth() * 0.5f, y, bg.getMinWidth(), height);
                bgTopHeight = bg.getTopHeight();
                bgBottomHeight = bg.getBottomHeight();
                positionHeight -= bgTopHeight + bgBottomHeight;
            }

            float knobHeightHalf = 0;
            if (knob == null) {
                knobHeightHalf = knobBefore == null ? 0 : knobBefore.getMinHeight() * 0.5f;
                position = (positionHeight - knobHeightHalf) * percent;
                position = Math.min(positionHeight - knobHeightHalf, position);
            } else {
                knobHeightHalf = knobHeight * 0.5f;
                position = (positionHeight - knobHeight) * percent;
                position = Math.min(positionHeight - knobHeight, position) + bgBottomHeight;
            }
            position = Math.max(Math.min(0, bgBottomHeight), position);

            if (knobBefore != null) {
                if (round) {
                    knobBefore.draw(batch, Math.round(x + (width - knobBefore.getMinWidth()) * 0.5f), Math.round(y + bgTopHeight),
                            Math.round(knobBefore.getMinWidth()), Math.round(position + knobHeightHalf));
                } else {
                    knobBefore.draw(batch, x + (width - knobBefore.getMinWidth()) * 0.5f, y + bgTopHeight, knobBefore.getMinWidth(),
                            position + knobHeightHalf);
                }
            }
            if (knobAfter != null) {
                if (round) {
                    knobAfter.draw(batch, Math.round(x + (width - knobAfter.getMinWidth()) * 0.5f),
                            Math.round(y + position + knobHeightHalf), Math.round(knobAfter.getMinWidth()),
                            Math.round(height - position - knobHeightHalf));
                } else {
                    knobAfter.draw(batch, x + (width - knobAfter.getMinWidth()) * 0.5f, y + position + knobHeightHalf,
                            knobAfter.getMinWidth(), height - position - knobHeightHalf);
                }
            }
            if (knob != null) {
                if (round) {
                    knob.draw(batch, Math.round(x + (width - knobWidth) * 0.5f), Math.round(y + position), Math.round(knobWidth),
                            Math.round(knobHeight));
                } else
                    knob.draw(batch, x + (width - knobWidth) * 0.5f, y + position, knobWidth, knobHeight);
            }
        } else {
            float positionWidth = width;

            float bgLeftWidth = 0, bgRightWidth = 0;
            if (bg != null) {
                if (round)
                    bg.draw(batch, x, Math.round(y + (height - bg.getMinHeight()) * 0.5f), width, Math.round(bg.getMinHeight()));
                else
                    bg.draw(batch, x, y + (height - bg.getMinHeight()) * 0.5f, width, bg.getMinHeight());
                bgLeftWidth = bg.getLeftWidth();
                bgRightWidth = bg.getRightWidth();
                positionWidth -= bgLeftWidth + bgRightWidth;
            }

            float knobWidthHalf = 0;
            if (knob == null) {
                knobWidthHalf = knobBefore == null ? 0 : knobBefore.getMinWidth() * 0.5f;
                position = (positionWidth - knobWidthHalf) * percent;
                position = Math.min(positionWidth - knobWidthHalf, position);
            } else {
                knobWidthHalf = knobWidth * 0.5f;
                position = (positionWidth - knobWidth) * percent;
                position = Math.min(positionWidth - knobWidth, position) + bgLeftWidth;
            }
            position = Math.max(Math.min(0, bgLeftWidth), position);

            if (knobBefore != null) {
                if (round) {
                    knobBefore.draw(batch, Math.round(x + bgLeftWidth), Math.round(y + (height - knobBefore.getMinHeight()) * 0.5f),
                            Math.round(position + knobWidthHalf), Math.round(knobBefore.getMinHeight()));
                } else {
                    knobBefore.draw(batch, x + bgLeftWidth, y + (height - knobBefore.getMinHeight()) * 0.5f, position + knobWidthHalf,
                            knobBefore.getMinHeight());
                }
            }
            if (knobAfter != null) {
                if (round) {
                    knobAfter.draw(batch, Math.round(x + position + knobWidthHalf),
                            Math.round(y + (height - knobAfter.getMinHeight()) * 0.5f), Math.round(width - position - knobWidthHalf),
                            Math.round(knobAfter.getMinHeight()));
                } else {
                    knobAfter.draw(batch, x + position + knobWidthHalf, y + (height - knobAfter.getMinHeight()) * 0.5f,
                            width - position - knobWidthHalf, knobAfter.getMinHeight());
                }
            }
            if (knob != null) {
                if (round) {
                    knob.draw(batch, Math.round(x + position), Math.round(y + (height - knobHeight) * 0.5f), Math.round(knobWidth),
                            Math.round(knobHeight));
                } else
                    knob.draw(batch, x + position, y + (height - knobHeight) * 0.5f, knobWidth, knobHeight);
            }
        }
    }

    public float getValue() {
        return value;
    }

    /**
     * If {@link #setAnimateDuration(float) animating} the progress bar value, this returns the value current displayed.
     */
    public float getVisualValue() {
        if (animateTime > 0)
            return animateInterpolation.apply(animateFromValue, value, 1 - animateTime / animateDuration);
        return value;
    }

    public float getPercent() {
        if (min == max) return 0;
        return (value - min) / (max - min);
    }

    public float getVisualPercent() {
        if (min == max) return 0;
        return visualInterpolation.apply((getVisualValue() - min) / (max - min));
    }

    protected Drawable getKnobDrawable() {
        return (disabled && style.disabledKnob != null) ? style.disabledKnob : style.knob;
    }

    /**
     * Returns progress bar visual position within the range.
     */
    protected float getKnobPosition() {
        return this.position;
    }

    /**
     * Sets the progress bar position, rounded to the nearest step size and clamped to the minimum and maximum values.
     * {@link #clamp(float)} can be overridden to allow values outside of the progress bar's min/max range.
     *
     * @return false if the value was not changed because the progress bar already had the value or it was canceled by a
     * listener.
     */
    public boolean setValue(float value, boolean fireEvent) {
        if (value<min){
            if (this.value==min)return false;
        }
        if (value>max){
            if (this.value==max)return false;
        }
        value = clamp(Math.round(value / stepSize) * stepSize);
        float oldValue = this.value;
        if (value == oldValue) return false;
        float oldVisualValue = getVisualValue();
        this.value = value;
        ChangeEvent changeEvent = Pools.obtain(ChangeEvent.class);
        if (fireEvent){
            boolean cancelled=fire(changeEvent);
            if (cancelled){
                this.value=oldValue;
            }else if (animateDuration > 0) {
                animateFromValue = oldVisualValue;
                animateTime = animateDuration;
            }
            Pools.free(changeEvent);
            return !cancelled;
        }else if (animateDuration > 0) {
            animateFromValue = oldVisualValue;
            animateTime = animateDuration;
        }
        return true;
    }

    /**
     * Clamps the value to the progress bar's min/max range. This can be overridden to allow a range different from the progress
     * bar knob's range.
     */
    protected float clamp(float value) {
        return MathUtils.clamp(value, min, max);
    }

    /**
     * Sets the range of this progress bar. The progress bar's current value is clamped to the range.
     */
    public void setRange(float min, float max) {
        if (min > max)
            throw new IllegalArgumentException("min must be <= max: " + min + " <= " + max);
        this.min = min;
        this.max = max;
        if (value < min)
            setValue(min,true);
        else if (value > max) setValue(max,true);
    }

    public void setStepSize(float stepSize) {
        if (stepSize <= 0) throw new IllegalArgumentException("steps must be > 0: " + stepSize);
        this.stepSize = stepSize;
    }

    public float getPrefWidth() {
        if (vertical) {
            final Drawable knob = getKnobDrawable();
            final Drawable bg = (disabled && style.disabledBackground != null) ? style.disabledBackground : style.background;
            return Math.max(knob == null ? 0 : knob.getMinWidth(), bg == null ? 0 : bg.getMinWidth());
        } else
            return 140;
    }

    public float getPrefHeight() {
        if (vertical)
            return 140;
        else {
            final Drawable knob = getKnobDrawable();
            final Drawable bg = (disabled && style.disabledBackground != null) ? style.disabledBackground : style.background;
            return Math.max(knob == null ? 0 : knob.getMinHeight(), bg == null ? 0 : bg.getMinHeight());
        }
    }

    public float getMinValue() {
        return this.min;
    }

    public float getMaxValue() {
        return this.max;
    }

    public float getStepSize() {
        return this.stepSize;
    }

    public void setAnimateDuration(float duration) {
        this.animateDuration = duration;
    }

    /**
     * Sets the interpolation to use for {@link #setAnimateDuration(float)}.
     */
    public void setAnimateInterpolation(Interpolation animateInterpolation) {
        if (animateInterpolation == null)
            throw new IllegalArgumentException("animateInterpolation cannot be null.");
        this.animateInterpolation = animateInterpolation;
    }

    /**
     * Sets the interpolation to use for display.
     */
    public void setVisualInterpolation(Interpolation interpolation) {
        this.visualInterpolation = interpolation;
    }

    /**
     * If true (the default), inner Drawable positions and sizes are rounded to integers.
     */
    public void setRound(boolean round) {
        this.round = round;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isDisabled() {
        return disabled;
    }

    /**
     * True if the progress bar is vertical, false if it is horizontal.
     **/
    public boolean isVertical() {
        return vertical;
    }

    /**
     * The style for a progress bar, see {@link com.badlogic.gdx.scenes.scene2d.ui.ProgressBar}.
     *
     * @author mzechner
     * @author Nathan Sweet
     */
    static public class ProgressBarStyle {
        /**
         * The progress bar background, stretched only in one direction. Optional.
         */
        public Drawable background;
        /**
         * Optional.
         **/
        public Drawable disabledBackground;
        /**
         * Optional, centered on the background.
         */
        public Drawable knob, disabledKnob;
        /**
         * Optional.
         */
        public Drawable knobBefore, knobAfter, disabledKnobBefore, disabledKnobAfter;

        public ProgressBarStyle() {
        }

        public ProgressBarStyle(Drawable background, Drawable knob) {
            this.background = background;
            this.knob = knob;
        }

        public ProgressBarStyle(com.badlogic.gdx.scenes.scene2d.ui.ProgressBar.ProgressBarStyle style) {
            this.background = style.background;
            this.disabledBackground = style.disabledBackground;
            this.knob = style.knob;
            this.disabledKnob = style.disabledKnob;
            this.knobBefore = style.knobBefore;
            this.knobAfter = style.knobAfter;
            this.disabledKnobBefore = style.disabledKnobBefore;
            this.disabledKnobAfter = style.disabledKnobAfter;
        }
    }
}
