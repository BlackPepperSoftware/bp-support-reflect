package uk.co.blackpepper.support.reflect;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import uk.co.blackpepper.support.reflect.AnnotationAccessor.InvocationInfo;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static uk.co.blackpepper.support.reflect.AnnotationAccessor.annotation;
import static uk.co.blackpepper.support.reflect.AnnotationAccessor.on;

public class AnnotationAccessorTest {

	@Retention(RetentionPolicy.RUNTIME)
	public @interface TestAnnotation {
		// simple annotation for unit tests
	}
	
	public static class TestClass {
		
		public TestReturnType noAnnotation() {
			return null;
		}
		
		@TestAnnotation
		public TestReturnType hasAnnotation() {
			return null;
		}
	}
	
	public static class TestReturnType {
		// simple return type
	}
	
	private ExpectedException thrown = ExpectedException.none();
	
	@Rule
	public ExpectedException getThrown() {
		return thrown;
	}
	
	@Test
	public void annotationWhenPresentReturnsAnnotation() throws NoSuchMethodException {
		InvocationInfo invocationInfo = mock(InvocationInfo.class);
		when(invocationInfo.getInvokedMethod()).thenReturn(TestClass.class.getMethod("hasAnnotation"));
		
		assertThat(annotation(TestAnnotation.class, invocationInfo), is(instanceOf(TestAnnotation.class)));
	}
	
	@Test
	public void annotationWhenNotPresentReturnsNull() throws NoSuchMethodException {
		InvocationInfo invocationInfo = mock(InvocationInfo.class);
		when(invocationInfo.getInvokedMethod()).thenReturn(TestClass.class.getMethod("noAnnotation"));
		
		assertThat(annotation(TestAnnotation.class, invocationInfo), is(nullValue()));
	}
	
	@Test
	public void onWithClassReturnsInstance() {
		assertThat(on(TestClass.class), is(instanceOf(TestClass.class)));
	}
	
	@Test
	public void onWithClassThenMethodReturnsInstance() {
		assertThat(on(TestClass.class).noAnnotation(), is(instanceOf(TestReturnType.class)));
	}
	
	@Test
	public void onWithClassThenMethodReturnsInvocationInfo() {
		assertThat(on(TestClass.class).noAnnotation(), is(instanceOf(InvocationInfo.class)));
	}
	
	@Test
	public void onWithClassThenMethodReturnsInvocationInfoWithMethod() throws NoSuchMethodException {
		InvocationInfo actual = (InvocationInfo) on(TestClass.class).noAnnotation();
		
		assertThat(actual.getInvokedMethod(), is(TestClass.class.getMethod("noAnnotation")));
	}
	
	@Test
	public void annotationOnWhenPresentReturnsAnnotation() {
		assertThat(annotation(TestAnnotation.class, on(TestClass.class).hasAnnotation()),
			is(instanceOf(TestAnnotation.class)));
	}
	
	@Test
	public void annotationOnWhenNotPresentReturnsNull() {
		assertThat(annotation(TestAnnotation.class, on(TestClass.class).noAnnotation()), is(nullValue()));
	}
}
