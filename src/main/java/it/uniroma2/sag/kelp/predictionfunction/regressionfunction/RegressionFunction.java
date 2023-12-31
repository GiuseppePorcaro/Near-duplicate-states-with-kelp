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

package it.uniroma2.sag.kelp.predictionfunction.regressionfunction;

import it.uniroma2.sag.kelp.data.example.Example;
import it.uniroma2.sag.kelp.predictionfunction.PredictionFunction;

/**
 * It is a generic regression prediction function, i.e. a function that, given an instance, can provide a real score 
 * associated to any of the properties of interest
 * 
 * @author Simone Filice
 *
 */
public interface RegressionFunction extends PredictionFunction{

	@Override
	public RegressionOutput predict(Example example);
}
