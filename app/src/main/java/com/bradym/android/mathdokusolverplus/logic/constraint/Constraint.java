package com.bradym.android.mathdokusolverplus.logic.constraint;

import com.bradym.android.mathdokusolverplus.logic.Variable;

import java.util.Collection;
import java.util.HashSet;

/**
 * Generic constraint that requires the basic functionality to be implemented in child classes.
 *
 */
public abstract class Constraint {

    private boolean enabled = false;

    public boolean bordered = true;
    public final HashSet<Variable> scope;

    Constraint(Collection<Variable> scope) {
        this.scope = new HashSet<>(scope.size());
        this.scope.addAll(scope);
        enable();
    }

    public void reset() {
    }

    public void enable() {
        if (!enabled) {
            for (Variable var : scope) {
                var.constraints.add(this);
            }
            enabled = true;
        }
    }

    public void disable() {
        if (enabled) {
            for (Variable var : scope) {
                var.constraints.remove(this);
            }
            enabled = false;
        }
    }

    /*
    Validate the constraint against current assignments and whichever heuristics are being used
     */
    protected abstract boolean onValidate(Variable var, int value);
    /*
    Take care of bookkeeping when a variable is going to be assigned a value
     */
    protected abstract void onAssignment(Variable var, int value);
    /*
    Take care of bookkeeping when a variable has been unassigned
     */
    protected abstract void onPopAssignment(Variable var, int value);

    protected abstract void onDomainPruning(Variable var, int value);

    protected abstract void onRestoredDomain(Variable var, Variable.Domain oldDomain);

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[ ");
        for (Variable v : scope) {
            sb.append(v.index).append(" ");
        }
        sb.append("]");

        return sb.toString().trim();
    }

    public abstract String cellString();

    public final boolean validateAssignment(Variable var, int value) {
        return onValidate(var, value);
    }

    public final void updateAssignment(Variable var, int value) {
        onAssignment(var, value);
    }

    public final void popAssignment(Variable var, int value) {
        onPopAssignment(var, value);
    }

    public final void updateRestoredDomain(Variable var, Variable.Domain oldDomain) {
        onRestoredDomain(var, oldDomain);
    }

    public void updatePrunedValue(Variable var, int value) {
        onDomainPruning(var, value);
    }
}
