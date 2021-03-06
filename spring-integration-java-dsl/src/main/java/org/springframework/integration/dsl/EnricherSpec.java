package org.springframework.integration.dsl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.expression.Expression;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.integration.dsl.core.IntegrationComponentSpec;
import org.springframework.integration.transformer.ContentEnricher;
import org.springframework.integration.transformer.support.AbstractHeaderValueMessageProcessor;
import org.springframework.integration.transformer.support.ExpressionEvaluatingHeaderValueMessageProcessor;
import org.springframework.integration.transformer.support.HeaderValueMessageProcessor;
import org.springframework.integration.transformer.support.StaticHeaderValueMessageProcessor;
import org.springframework.messaging.MessageChannel;
import org.springframework.util.Assert;

/**
 * @author Artem Bilan
 */
public class EnricherSpec extends IntegrationComponentSpec<EnricherSpec, ContentEnricher> {

	private final static SpelExpressionParser PARSER = new SpelExpressionParser();

	private final ContentEnricher enricher = new ContentEnricher();

	private final Map<String, Expression> propertyExpressions = new HashMap<String, Expression>();

	private final Map<String, HeaderValueMessageProcessor<?>> headerExpressions = new HashMap<String, HeaderValueMessageProcessor<?>>();

	EnricherSpec() {
	}

	public EnricherSpec requestChannel(MessageChannel requestChannel) {
		this.enricher.setRequestChannel(requestChannel);
		return _this();
	}

	public EnricherSpec requestChannel(String requestChannel) {
		this.enricher.setRequestChannelName(requestChannel);
		return _this();
	}

	public EnricherSpec replyChannel(MessageChannel replyChannel) {
		this.enricher.setReplyChannel(replyChannel);
		return _this();
	}

	public EnricherSpec replyChannel(String replyChannel) {
		this.enricher.setReplyChannelName(replyChannel);
		return _this();
	}

	public EnricherSpec requestTimeout(Long requestTimeout) {
		this.enricher.setRequestTimeout(requestTimeout);
		return _this();
	}

	public EnricherSpec replyTimeout(Long replyTimeout) {
		this.enricher.setReplyTimeout(replyTimeout);
		return _this();
	}

	public EnricherSpec requestPayloadExpression(String requestPayloadExpression) {
		this.enricher.setRequestPayloadExpression(PARSER.parseExpression(requestPayloadExpression));
		return _this();
	}

	public EnricherSpec shouldClonePayload(boolean shouldClonePayload) {
		this.enricher.setShouldClonePayload(shouldClonePayload);
		return _this();
	}

	public EnricherSpec property(String key, String value) {
		this.propertyExpressions.put(key, new LiteralExpression(value));
		return _this();
	}

	public EnricherSpec propertyExpression(String key, String expression) {
		Assert.notNull(key);
		this.propertyExpressions.put(key, PARSER.parseExpression(expression));
		return _this();
	}

	public EnricherSpec header(String name, Object value) {
		return this.header(name, value, null);
	}

	public EnricherSpec header(String name, Object value, Boolean overwrite) {
		AbstractHeaderValueMessageProcessor<?> headerValueMessageProcessor = new StaticHeaderValueMessageProcessor<Object>(value);
		headerValueMessageProcessor.setOverwrite(overwrite);
		return this.header(name, headerValueMessageProcessor);
	}

	public EnricherSpec headerExpression(String name, String expression) {
		return this.headerExpression(name, expression, null, null);
	}

	public EnricherSpec headerExpression(String name, String expression, Boolean overwrite) {
		return this.headerExpression(name, expression, overwrite, null);
	}

	public EnricherSpec headerExpression(String name, String expression, Class<?> type) {
		return this.headerExpression(name, expression, null, type);
	}

	public <T> EnricherSpec headerExpression(String name, String expression, Boolean overwrite, Class<T> type) {
		AbstractHeaderValueMessageProcessor<T> headerValueMessageProcessor =
				new ExpressionEvaluatingHeaderValueMessageProcessor<T>(expression, type);
		headerValueMessageProcessor.setOverwrite(overwrite);
		return this.header(name, headerValueMessageProcessor);
	}

	public EnricherSpec header(String name, HeaderValueMessageProcessor<?> headerValueMessageProcessor) {
		Assert.notNull(name);
		this.headerExpressions.put(name, headerValueMessageProcessor);
		return _this();
	}


	@Override
	protected ContentEnricher doGet() {
		this.enricher.setPropertyExpressions(this.propertyExpressions);
		this.enricher.setHeaderExpressions(this.headerExpressions);
		return this.enricher;
	}

}
