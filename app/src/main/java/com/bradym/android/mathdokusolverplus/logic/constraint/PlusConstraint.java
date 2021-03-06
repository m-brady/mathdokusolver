package com.bradym.android.mathdokusolverplus.logic.constraint;

import com.bradym.android.mathdokusolverplus.logic.Variable;

import java.util.Collection;

/**
 * Addition constraint.
 */
public class PlusConstraint extends CommutativeConstraint {

    public static String op;

    public PlusConstraint(Collection<Variable> scope, int constraint) {
        super(scope, constraint, 0);
    }

    @Override
    protected boolean onValidate(Variable var, int value) {
        int newCurrent = getCurrentValue() + value;
        int cumMax = getCumulativeMax() - var.max();
        int cumMin = getCumulativeMin() - var.min();
        return (newCurrent + cumMax >= constraint) &&
                (newCurrent + cumMin <= constraint);
    }

    @Override
    protected int op(int current, int remove, int add) {
        return current - remove + add;
    }

    @Override
    protected String opString() {
        return op;
    }
}
