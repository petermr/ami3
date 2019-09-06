package org.contentmine.ami.tools;

import org.contentmine.eucl.euclid.IntRange;

import picocli.CommandLine.ITypeConverter;

public class IntRangeConverter implements ITypeConverter<IntRange> {
    public IntRange convert(String value) throws Exception {
        IntRange intRange = IntRange.parse(value);
        return intRange;
	}
}
