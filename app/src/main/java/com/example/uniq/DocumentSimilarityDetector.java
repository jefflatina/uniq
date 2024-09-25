package com.example.uniq;

import org.deeplearning4j.models.word2vec.Word2Vec;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.ops.transforms.Transforms;
import java.util.List;
public class DocumentSimilarityDetector {

    private static Word2Vec word2VecModel;

    public DocumentSimilarityDetector(Word2Vec word2VecModel) {
        this.word2VecModel = word2VecModel;
    }

    public double calculateDocumentSimilarity(String document1, String document2) {
        List<String> tokens1 = TextPreprocessor.preprocess(document1);
        List<String> tokens2 = TextPreprocessor.preprocess(document2);

        // Convert document tokens to vectors
        INDArray vector1 = getAverageWordVector(tokens1);
        INDArray vector2 = getAverageWordVector(tokens2);

        // Calculate cosine similarity between the vectors
        double similarity = Transforms.cosineSim(vector1, vector2);
        return similarity;
    }


    public static INDArray getAverageWordVector(List<String> tokens) {
        int vectorSize = word2VecModel.getLayerSize();
        INDArray sumVector = word2VecModel.getLookupTable().getWeights().getRow(0).dup(); // Initialize with zeros

        int count = 0;
        for (String token : tokens) {
            if (word2VecModel.hasWord(token)) {
                INDArray wordVector = word2VecModel.getWordVectorMatrix(token);
                sumVector.addi(wordVector);
                count++;
            }
        }

        if (count > 0) {
            sumVector.divi(count); // Calculate the average
        }

        return sumVector;
    }

}
