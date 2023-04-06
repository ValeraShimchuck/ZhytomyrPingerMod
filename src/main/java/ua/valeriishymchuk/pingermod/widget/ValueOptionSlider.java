package ua.valeriishymchuk.pingermod.widget;

import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

import java.util.function.Consumer;
import java.util.function.Function;

public class ValueOptionSlider extends SliderWidget {

    private final Function<Double, Text> updateMessageFun;
    private final Consumer<Double> applyValue;

    public ValueOptionSlider(int x, int y, int width, int height, Text text, double value, Function<Double, Text> updateMessageFun, Consumer<Double> applyValue) {
        super(x, y, width, height, text, value);
        this.updateMessageFun = updateMessageFun;
        this.applyValue = applyValue;
        updateMessage();
    }

    @Override
    protected void updateMessage() {
        setMessage(updateMessageFun.apply(value));
    }

    @Override
    protected void applyValue() {
        applyValue.accept(value);
    }
}
