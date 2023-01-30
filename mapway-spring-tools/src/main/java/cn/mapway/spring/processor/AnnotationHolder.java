package cn.mapway.spring.processor;

import com.google.auto.common.AnnotationMirrors;
import com.google.auto.common.AnnotationValues;
import com.google.auto.common.MoreElements;
import com.google.common.base.Optional;

import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnnotationHolder {
    Map<ExecutableElement, AnnotationValue> valuesWithDefaults=new HashMap<>();
    boolean hasValues=false;
    public static AnnotationHolder createFromElement(Element element,Class<? extends Annotation> annotation) {
        AnnotationHolder annotationHolder = new AnnotationHolder();
        Optional<AnnotationMirror> rpcPackage = MoreElements.getAnnotationMirror(element, annotation);
        if(!rpcPackage.isPresent())
        {
            annotationHolder.hasValues = false;
            return annotationHolder;
        }
        annotationHolder.hasValues=true;
        annotationHolder.valuesWithDefaults = AnnotationMirrors.getAnnotationValuesWithDefaults(rpcPackage.get());
        return annotationHolder;
    }
    public boolean isPresent(){
        return hasValues;
    }


    public Boolean getBoolean(String fieldName) {
        java.util.Optional<ExecutableElement> includes = valuesWithDefaults.keySet().stream().filter(key -> key.getSimpleName().toString().equals(fieldName)).findAny();
        if(includes.isPresent())
        {
            AnnotationValue annotationValue = valuesWithDefaults.get(includes.get());
            return AnnotationValues.getBoolean(annotationValue);
        }
        return false;
    }

    public String getString(String fieldName) {
        java.util.Optional<ExecutableElement> includes = valuesWithDefaults.keySet().stream().filter(key -> key.getSimpleName().toString().equals(fieldName)).findAny();
        if(includes.isPresent())
        {
            AnnotationValue annotationValue = valuesWithDefaults.get(includes.get());
            return AnnotationValues.getString(annotationValue);
        }
        return "";
    }
    public  List<String> getStrings(String fieldName)
    {
        List result= new ArrayList();
        java.util.Optional<ExecutableElement> includes = valuesWithDefaults.keySet().stream().filter(key -> key.getSimpleName().toString().equals(fieldName)).findAny();
        if(includes.isPresent())
        {
            AnnotationValue annotationValue = valuesWithDefaults.get(includes.get());
            return AnnotationValues.getStrings(annotationValue);
        }
        return result;
    }
    public  List<VariableElement> getEnums(String fieldName)
    {
        List result= new ArrayList();
        java.util.Optional<ExecutableElement> includes = valuesWithDefaults.keySet().stream().filter(key -> key.getSimpleName().toString().equals(fieldName)).findAny();
        if(includes.isPresent())
        {
            AnnotationValue annotationValue = valuesWithDefaults.get(includes.get());
            return AnnotationValues.getEnums(annotationValue);
        }
        return result;
    }
    public  List<Integer> getIntegers(String fieldName)
    {
        List result= new ArrayList();
        java.util.Optional<ExecutableElement> includes = valuesWithDefaults.keySet().stream().filter(key -> key.getSimpleName().toString().equals(fieldName)).findAny();
        if(includes.isPresent())
        {
            AnnotationValue annotationValue = valuesWithDefaults.get(includes.get());
            return AnnotationValues.getInts(annotationValue);
        }
        return result;
    }
    public  Integer getInteger(String fieldName)
    {
        java.util.Optional<ExecutableElement> includes = valuesWithDefaults.keySet().stream().filter(key -> key.getSimpleName().toString().equals(fieldName)).findAny();
        if(includes.isPresent())
        {
            AnnotationValue annotationValue = valuesWithDefaults.get(includes.get());
            return AnnotationValues.getInt(annotationValue);
        }
        return 0;
    }

    public List<Boolean> getBooleans(String fieldName)
    {
        List result= new ArrayList();
        java.util.Optional<ExecutableElement> includes = valuesWithDefaults.keySet().stream().filter(key -> key.getSimpleName().toString().equals(fieldName)).findAny();
        if(includes.isPresent())
        {
            AnnotationValue annotationValue = valuesWithDefaults.get(includes.get());
            return AnnotationValues.getBooleans(annotationValue);
        }
        return result;
    }
    public List<DeclaredType> getClasses(String fieldName)
    {
        List result= new ArrayList();
        java.util.Optional<ExecutableElement> includes = valuesWithDefaults.keySet().stream().filter(key -> key.getSimpleName().toString().equals(fieldName)).findAny();
        if(includes.isPresent())
        {
            AnnotationValue annotationValue = valuesWithDefaults.get(includes.get());
            return AnnotationValues.getTypeMirrors(annotationValue);
        }
        return result;
    }

}
