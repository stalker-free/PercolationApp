package hk.util;

import hk.Lattice;
import hk.cell.*;

import java.util.regex.*;

public class LatticeParser
{
	/**
	 * Load lattice from char sequence.
	 * @param seq source file.
	 * @return ready lattice for applying Hoshen-Kopelman algorithm.
	 * @throws NumberFormatException
	 */
	public static Lattice parse(CharSequence seq) throws NumberFormatException
	{
		Pattern linePattern = Pattern.compile(";"),
			colonPattern = Pattern.compile(",");

		String[] lines = linePattern.split(seq);
		String[] elements = colonPattern.split(lines[0]);
		int rows = lines.length, cols = elements.length;
		Cell[][] cells = new Cell[rows][cols];

		int i = 0;
		while(true)
		{
			for(int j = 0 ; j < cols ; j++)
			{
				cells[i][j] = new Cell(Integer.valueOf(elements[j]));
			}
			if(++i >= rows || i < 0) break;
			elements = colonPattern.split(lines[i]);
		}

		return new Lattice(cells);
	}
}
