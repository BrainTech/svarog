package org.signalml.domain.montage.generators;

/**
 * An enum holding the types of {@link IMontageGenerator MontageGenerators} available
 * in Svarog. ({@link GeneratorType#AVERAGE_REFERENCE} corresponds to the
 * {@link AverageReferenceMontageGenerator}.
 *
 * @author Piotr Szachewicz
 */
enum GeneratorType {
	AVERAGE_REFERENCE,
	SINGLE_REFERENCE,
	BIPOLAR_REFERENCE,
	RAW;
}
