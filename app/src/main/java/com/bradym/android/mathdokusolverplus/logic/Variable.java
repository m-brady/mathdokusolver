package com.bradym.android.mathdokusolverplus.logic;

import com.bradym.android.mathdokusolverplus.PuzzleCell;
import com.bradym.android.mathdokusolverplus.logic.constraint.Constraint;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Variable object to be used in the solving process.
 * Keeps track of attached constraints, cell, and past domains.
 */
public final class Variable {

    public final List<Constraint> constraints;
    public final int index;
    public final PuzzleCell cell;
    public Domain domain;

    private final int max;
    private final Deque<Domain> domainHistory;
    private boolean domainSaved = false;

    public Variable(int index, PuzzleCell cell, int max) {
        this.index = index;
        this.cell = cell;
        this.constraints = new ArrayList<>();
        this.domainHistory = new ArrayDeque<>();
        this.domain = new Domain(~(~0 << max));
        this.max = max;
    }

    public void reset() {
        this.domainHistory.clear();
        this.domain = new Domain(~(~0 << max));
        domainSaved = false;
    }

    public int nextValid(int i) {
        return domain.nextValid(i);
    }

    public void lock() {
        domainSaved = false;
    }

    public void prune(int val) {
        if (!domainSaved) {
            domainHistory.addLast(new Domain(domain));
            domainSaved = true;
        }
        domain.prune(val);
        for (Constraint constraint : constraints) {
            constraint.updatePrunedValue(this, val);
        }
    }

    public void restoreDomain() {
        domainSaved = false;
        Domain oldDomain = domain;
        domain = domainHistory.pollLast();
        for (Constraint constraint : constraints) {
            constraint.updateRestoredDomain(this, oldDomain);
        }
    }

    public boolean assign(int val) {
        boolean valid = true;

        for (Constraint constraint : constraints) {
            if (!constraint.validateAssignment(this, val)) {
                valid = false;
                break;
            }
        }
        if (valid) {
            for (Constraint constraint : constraints) {
                constraint.updateAssignment(this, val);
            }
            domainHistory.addLast(domain);
            domain = new Domain(1 << (val - 1));
            domain.value = val;

        }
        return valid;
    }

    public void unAssign() {
        Domain oldDomain = domain;
        domain = domainHistory.pollLast();
        for (Constraint constraint : constraints) {
            constraint.popAssignment(this, oldDomain.value);
        }
    }

    public int value() {
        return domain.value;
    }

    public int max() {
        return domain.max;
    }

    public int min() {
        return domain.min;
    }

    public int domainSize() {
        return domain.size;
    }

    @Override
    public String toString() {
        return "VARIABLE " + index + " DOMAIN " + domain.toString() + " VALUE " + domain.value;
    }

    public final class Domain {

        public int domain;
        public int max;
        public int min;
        public int size;
        public int value = -1;


        public Domain(int domain) {
            this.domain = domain;
            this.max = 32 - Integer.numberOfLeadingZeros(domain);
            this.min = 1 + Integer.numberOfTrailingZeros(domain);
            this.size = Integer.bitCount(domain);
        }

        public Domain(Domain domain) {
            this.domain = domain.domain;
            this.max = domain.max;
            this.min = domain.min;
            this.size = domain.size;
        }

        public void prune(int val) {
            domain = domain & ~(1 << (val - 1));
            if (val == max) {
                max = domain == 0 ? 0 : 32 - Integer.numberOfLeadingZeros(domain);
            }
            if (val == min) {
                min = domain == 0 ? 0 : 1 + Integer.numberOfTrailingZeros(domain);
            }
            size--;
        }

        public int nextValid(int i) {
            int shifted = domain >> i;
            return shifted == 0 ? -1 : i + Integer.numberOfTrailingZeros(shifted);
        }

        @Override
        public String toString() {
            return Integer.toBinaryString(domain);
        }


    }
}


