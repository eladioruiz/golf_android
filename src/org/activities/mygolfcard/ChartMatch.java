/**
 * Copyright (C) 2009, 2010 SC 4ViewSoft SRL
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.activities.mygolfcard;


import java.util.ArrayList;
import java.util.List;

import org.charts.mygolfcard.*;
import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

/**
 * Average temperature demo chart.
 */
public class ChartMatch extends AbstractChart {

	
	private static int TYPE_STROKES = 1;
	private static int TYPE_TOTAL 	= 2;
	private double maxValue = 0.0;
	private double minValue = 999.0;

	/**
	 * Returns the chart name.
	 * @return the chart name
	 */
	public String getName() {
		return "Comparativa Partido";
	}

	/**
	 * Returns the chart description.
	 * @return the chart description
	 */
	public String getDesc() {
		return "Comparativa de partido por jugador";
	}

	/**
	 * Executes the chart demo.
	 * @param context the context
	 * @return the built intent
	 */
	public Intent execute(Context context) {
		return null;
	}

	/**
	 * Executes the chart demo.
	 * @param context the context
	 * @param match_id match identifier
	 * @return the built intent
	 */
	public Intent execute(Context context, org.classes.mygolfcard.Match currentMatch, int type) {
		int totalPlayers = currentMatch.getNumPlayers();
		String[] titles = new String[totalPlayers];

		maxValue = 0.0;
		minValue = 999.0;
		
		org.classes.mygolfcard.Player[] pls = new org.classes.mygolfcard.Player[currentMatch.getPlayers().length];

		pls = currentMatch.getPlayers();
		for (int i=0; i < totalPlayers; i++) {
			if (pls[i] != null) {
				titles[i] = pls[i].getPlayerName();
			}
		}
		
		List<double[]> values = new ArrayList<double[]>();
		List<double[]> x = new ArrayList<double[]>();
		
		// Eje X - Literales por cada columna
		// En nuestro caso, el número de los hoyos 
		for (int i = 0; i < totalPlayers; i++) {
			double aux[] = new double[currentMatch.getHoles()];
			for (int j=0;j<currentMatch.getHoles();j++) {
				aux[j] = j + 1;
			}
			x.add(aux);
		}
		
		int strokes = 0;
		// Eje Y
		for (int i=0; i < totalPlayers; i++) {
			double aux[] = new double[currentMatch.getHoles()];
			
			org.classes.mygolfcard.Stroke[] auxStroke = new org.classes.mygolfcard.Stroke[18];
			if (!currentMatch.getLocalStorage()) {
				auxStroke = pls[i].getStrokes();
			}
			
			for (int j=0; j<currentMatch.getHoles(); j++) {
				if (currentMatch.getLocalStorage()) {
					strokes = currentMatch.getStrokesPerHole(pls[i].getPlayer_id(),j);
				}
				else
				{
					//org.classes.mygolfcard.Stroke[] auxStroke = pls[i].getStrokes();
					strokes = auxStroke[j].getStrokes();
				}
				
				if (type==TYPE_STROKES) {
					aux[j] = strokes;
				}
				else if (type==TYPE_TOTAL) {
					if (j>=1) {
						aux[j] = aux[j-1] + strokes;
					}
					else {
						aux[j] = strokes;
					}
				}
				
				if (aux[j] > maxValue) {
					maxValue = aux[j];
				}
				
				if (aux[j] < minValue) {
					minValue = aux[j];
				}
				
				//aux[j] = (double)j+i;
			}
			values.add(aux);
		}

/*		values.add(new double[] { 12.3, 12.5, 13.8, 16.8, 20.4, 24.4, 26.4, 26.1, 23.6, 20.3, 17.2,
				13.9 });
		values.add(new double[] { 10, 10, 12, 15, 20, 24, 26, 26, 23, 18, 14, 11 });
		values.add(new double[] { 5, 5.3, 8, 12, 17, 22, 24.2, 24, 19, 15, 9, 6 });
		values.add(new double[] { 9, 10, 11, 15, 19, 23, 26, 25, 22, 18, 13, 10 });
*/		
		int[] colorsAux = new int[] { Color.BLUE, Color.GREEN, Color.CYAN, Color.YELLOW };
		int[] colors = new int[totalPlayers];
		for (int i=0; i<totalPlayers; i++) {
			colors[i] = colorsAux[i];
		}
		
		PointStyle[] stylesAux = new PointStyle[] { PointStyle.CIRCLE, PointStyle.DIAMOND,
				PointStyle.TRIANGLE, PointStyle.SQUARE };
		PointStyle[] styles = new PointStyle[totalPlayers];
		for (int i=0; i<totalPlayers; i++) {
			styles[i] = stylesAux[i];
		}
		
		XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles);
		int length = renderer.getSeriesRendererCount();
		for (int i = 0; i < totalPlayers; i++) {
			((XYSeriesRenderer) renderer.getSeriesRendererAt(i)).setFillPoints(true);
		}
		setChartSettings(renderer, "Partido", "Hoyos", "Golpes", 0.5, ((double)currentMatch.getHoles()) + 0.5, minValue, maxValue,
				Color.LTGRAY, Color.GRAY);
		renderer.setXLabels(12);
		renderer.setYLabels(10);
		renderer.setShowGrid(true);

		Intent intent = ChartFactory.getLineChartIntent(context, buildDataset(titles, x, values),
				renderer, "Comparativa Partido");
		return intent;
	}

}
