package io.github.sukgu.support;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindBys;
import org.openqa.selenium.support.pagefactory.DefaultFieldDecorator;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;

public class ElementFieldDecorator extends DefaultFieldDecorator {

	public ElementFieldDecorator(ElementLocatorFactory factory) {
		super(factory);
	}
	
	@Override
	protected boolean isDecoratableList(Field field) {
	    if (!List.class.isAssignableFrom(field.getType())) {
	      return false;
	    }

	    // Type erasure in Java isn't complete. Attempt to discover the generic
	    // type of the list.
	    Type genericType = field.getGenericType();
	    if (!(genericType instanceof ParameterizedType)) {
	      return false;
	    }

	    Type listType = ((ParameterizedType) genericType).getActualTypeArguments()[0];

	    if (!WebElement.class.equals(listType)) {
	      return false;
	    }

	    if (field.getAnnotation(FindElementBy.class) == null &&
	    	field.getAnnotation(FindBy.class) == null &&
	        field.getAnnotation(FindBys.class) == null &&
	        field.getAnnotation(FindAll.class) == null) {
	      return false;
	    }

	    return true;
	  }

}
