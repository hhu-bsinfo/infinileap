package de.hhu.bsinfo.neutrino.bench;

public class ComplexNumber {

    public final double real;
    public final double imaginary;

    public ComplexNumber(double real, double imaginary) {
        this.real = real;
        this.imaginary = imaginary;
    }

    public ComplexNumber add(ComplexNumber addend) {
        return new ComplexNumber(real + addend.real, imaginary + addend.imaginary);
    }
}
