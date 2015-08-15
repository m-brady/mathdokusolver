package com.bradym.android.mathdokusolver.logic.constraint;

import com.bradym.android.mathdokusolver.logic.Variable;

import java.util.Collection;
import java.util.Map;

/**
 * Created by Michael on 7/25/2015.
 */
public class DivConstraint extends InverseConstraint {

    public static String op;

    public DivConstraint(Collection<Variable> scope, int constraint) {
        super(scope, constraint, 1);
    }

    @Override
    protected int op(int current, int remove, int add) {
        return current / remove * add;
    }

    @Override
    protected String opString() {
        return op;
    }

    @Override
    protected IntPair onValidateEntry(Variable var, int value, Map.Entry<Variable, IntPair> entry) {
        IntPair currentValue = entry.getValue();
        Variable top = entry.getKey();
        int num = currentValue.first;
        int den = currentValue.second;
        int adjustedCumMin = getCumulativeMin();
        int adjustedCumMax = getCumulativeMax();

        if (var.value() == -1) {
            adjustedCumMin /= var.min();
            adjustedCumMax /= var.max();
        }

        if (var == top) {
            if ((value % constraint == 0) && (value % den == 0)
                    && (value <= constraint * den * adjustedCumMax)
                && (value >= constraint * den * adjustedCumMin)) {
                return new IntPair(value, den);
            }
        } else {
            den *= value;
            if (num == -1) {
                int absMax = top.max();
                int absMin = top.min();
                adjustedCumMin /= absMin;
                adjustedCumMax /= absMax;
                if ((absMin <= constraint * den * adjustedCumMax) &&
                        (absMax >= constraint * den * adjustedCumMin)) {
                    return new IntPair(num, den);
                }
            } else {
                if ((num % den == 0) && (num <= constraint * den * adjustedCumMax) &&
                        (num >= constraint * den * adjustedCumMin)) {
                    return new IntPair(num, den);
                }
            }
        }
        return null;
    }
}
