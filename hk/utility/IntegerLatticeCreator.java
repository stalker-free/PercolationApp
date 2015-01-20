package hk.utility;

import hk.*;
import hk.cell.*;

public class IntegerLatticeCreator implements Runnable
{
	private static final Cell<Integer> zero = new IntegerCell(0);
	private CellRange<Integer> result;

	public IntegerLatticeCreator(CellRange<Integer> result){
		this.result = result;
	}

	@Override
	public void run(){
		Cell<Integer> currentCell;
		CellRange<Integer>.CellIterator it = (CellRange<Integer>.CellIterator)result.iterator();
		while(it.hasNext())
		{
			currentCell = it.next();

			// Skip non-reference cells
			if(!currentCell.isReference())
			{
				continue;
			}

			// Give to the current cell a label
			it.set(it.getCell());
		}
	}
}
