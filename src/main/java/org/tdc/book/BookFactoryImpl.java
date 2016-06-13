package org.tdc.book;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdc.config.book.BookConfig;
import org.tdc.modelinst.ModelInst;
import org.tdc.modelinst.ModelInstFactory;

/**
 * A {@link BookFactory} implementation.
 * 
 * <p>Creates {@link ModelInst} instances, as necessary, using provided {@link ModelInstFactory}.
 */
public class BookFactoryImpl implements BookFactory {
	
	private static final Logger log = LoggerFactory.getLogger(BookFactoryImpl.class);

	private final ModelInstFactory modelInstFactory;
	
	public BookFactoryImpl(ModelInstFactory modelInstFactory) {
		this.modelInstFactory = modelInstFactory;
	}

	@Override
	public Book getBook(BookConfig config) {
		return new BookImpl.BookBuilder(config, modelInstFactory).build();
	}
}
