package org.echanneling.designpattern.decorator;
public abstract class PrescriptionDecorator implements PrescriptionComponent {
    protected final PrescriptionComponent wrapped;
    protected PrescriptionDecorator(PrescriptionComponent wrapped) { this.wrapped = wrapped; }
}
