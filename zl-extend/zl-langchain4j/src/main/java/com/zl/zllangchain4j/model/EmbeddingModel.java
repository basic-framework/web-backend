package com.zl.zllangchain4j.model;

import ai.djl.nn.core.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.output.Response;

import java.util.List;

//public interface EmbeddingModel {
//    default Response<Embedding> embed(String text) {
//        return this.embed(TextSegment.from(text));
//    }
//
//    default Response<Embedding> embed(TextSegment textSegment) {
//        return null;
//    }
//
//    default Response<List<Embedding>> embedAll(List<TextSegment> texts) {
//        return null;
//    }
//
//    default int dimension() {
//        return ((Embedding)this.embed("test").content()).dimension();
//    }
//}