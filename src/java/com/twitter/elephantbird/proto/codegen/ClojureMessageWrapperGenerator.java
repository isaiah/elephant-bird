package com.twitter.elephantbird.proto.codegen;

import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.protobuf.DescriptorProtos.DescriptorProto;
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto;
import com.twitter.elephantbird.proto.util.FormattingStringBuffer;
import com.twitter.elephantbird.util.Strings;

public class ClojureMessageWrapperGenerator extends ProtoCodeGenerator {

	@Override
	public String getFilename() {
	    return String.format("cascalog/%s.clj", Strings.underscore(descriptorProto_.getName()));
	}

	@Override
	public String generateCode() {
	    FormattingStringBuffer sb = new FormattingStringBuffer();
	    sb.append("(ns cascalog.%s", clojurize(descriptorProto_.getName())).endl();
	    sb.append("  (import [%s.%s]))", packageName_, descriptorProto_.getName()).endl().endl();
	    sb.append(generateFieldWrappers(descriptorProto_)).endl();
		return sb.toString();
	}
	
	private String generateFieldWrappers(DescriptorProto descriptorProto, String ... outerMsgNames) {
	    FormattingStringBuffer sb = new FormattingStringBuffer();
	    for (FieldDescriptorProto field : descriptorProto.getFieldList()) {
	    	List<String> fnName = Lists.newArrayList(outerMsgNames);
	    	fnName.add(Strings.dasherize(field.getName()));
	    	sb.append("(defn %s [^%s e]", Joiner.on("-").join(fnName), descriptorProto.getName()).endl();
	    	sb.append("  (. e get%s))", Strings.camelize(field.getName())).endl();
	    }
	    
	    for (DescriptorProto nestedType : descriptorProto.getNestedTypeList()) {
	    	List<String> fnName = Lists.newArrayList(outerMsgNames);
	    	fnName.add(clojurize(nestedType.getName()));
	    	sb.append(generateFieldWrappers(nestedType, fnName.toArray(new String[]{}))).endl();
	    }
	    
	    return sb.toString();
	}
	
	private String clojurize(String name) {
		return Strings.dasherize(Strings.underscore(name));
	}

}
