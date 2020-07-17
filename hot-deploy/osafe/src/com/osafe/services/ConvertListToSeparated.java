package com.osafe.services;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.BoolCellProcessor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.DateCellProcessor;
import org.supercsv.cellprocessor.ift.DoubleCellProcessor;
import org.supercsv.cellprocessor.ift.LongCellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.util.CSVContext;

public class ConvertListToSeparated extends CellProcessorAdaptor implements DateCellProcessor, DoubleCellProcessor, LongCellProcessor, StringCellProcessor, BoolCellProcessor {
    Object separator = "";

    /**
     * To have the string <tt>""</tt> return when a null is encountered, use
     * this class as <code>
     * new ConvertNullTo("\"\"");
     * </code>
     *
     * @param separator
     *            the value to return in case the input is <tt>null</tt>.
     */
    public ConvertListToSeparated(final Object separator) {
        super();
        this.separator = separator;
    }

    /**
     * Constructor To have the string <tt>""</tt> return when a null is
     * encountered, use this class as <code>
     * new ConvertNullTo("\"\"");
     * </code>
     * <p>
     * If you need further processing of the value in case the value is not
     * null, you can link the processor with other processors such as <code>
     * new ConvertNullTo("\"\"", new Trim(3));
     * </code>
     *
     * @param returnValue
     *            the value to return in case the input is <tt>null</tt>.
     * @param next
     *            Chained cell processor.
     */
    public ConvertListToSeparated(final Object returnValue, final CellProcessor next) {
        super(next);
        this.separator = returnValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object execute(final Object value, final CSVContext context) {
        if (value == null || ((List) value).size() == 0) {
            return "";
        } else {
            return StringUtils.join(((List) value), (String)separator);
        }

//        return next.execute(value, context);
    }
}
