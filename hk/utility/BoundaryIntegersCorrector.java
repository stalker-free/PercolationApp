package hk.utility;

import hk.*;
import hk.cell.*;

import java.util.concurrent.*;

/**
 * Class which bounds nearest cell ranges among each other.
 */
public class BoundaryIntegersCorrector implements Runnable, Callable<Void>
{
	private static final Cell<Integer> zero = new IntegerCell(0);
	private CellRange<Integer> result;

	public BoundaryIntegersCorrector(CellRange<Integer> result){
		this.result = result;
	}

	@Override
	public void run(){
		Integer value;
		Cell<Integer> currentCell, ref;
		CellRange<Integer>.CellIterator it = (CellRange<Integer>.CellIterator)result.iterator();
		while(it.getCurrentX() == result.getStartX() && it.hasNext())
		{
			currentCell = it.next();
			value = currentCell.getValue();

			// Skip correct cells or zeros
			if(currentCell.isReference() || 0 == value)
			{
				continue;
			}

			ref = it.getNorth(zero, 0);
			if(null != ref.getValue() && 0 == ref.getValue())
			{
				continue;
			}

			// Make the current cell refer to another
			it.set(it.getNorthReference());
			IntegerUnionFindHelper.uniteLabels(value,
					it.getCell().getValue());
		}
	}

	@Override
	public Void call() throws Exception{
		run();
		return null;
	}
}
