/*
 * Copyright 2014 Simone Filice and Giuseppe Castellucci and Danilo Croce and Roberto Basili
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.uniroma2.sag.kelp.predictionfunction;

import it.uniroma2.sag.kelp.data.label.Label;

/**
 * It is a generic output provided by a machine learning systems on a test data
 * 
 * @author      Simone Filice
 */
public interface Prediction {
	
	/**
	 * Return the prediction score associated to a given label
	 * 
	 * @param label the label whose associated score is required
	 * @return the score associated to a given label
	 */
	public Float getScore(Label label);
}
