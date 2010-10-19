package tasks;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

import system.ResultImpl;
import api.Result;
import api.Task;

public class MandelbrotSetTask extends TaskBase<MandelbrotSetTask.MandelbrotSetChunk> implements
		Serializable {
	
	public class MandelbrotSetChunk implements Serializable{
		
		private static final long serialVersionUID = -6076247513686818330L;
		int x;
		int y;
		int[][] values;
		
		public MandelbrotSetChunk(int x,int y,int[][] values){
			this.x=x;
			this.y=y;
			this.values=values;
		}
		public int getX() {
			return x;
		}
		public void setX(int x) {
			this.x = x;
		}
		
		public int getY() {
			return y;
		}
		public void setY(int y) {
			this.y = y;
		}
		
		public int[][] getValues() {
			return values;
		}
		public void setValues(int[][] values) {
			this.values = values;
		}
		
		
	}
	

	private static final long serialVersionUID = -2438392380951095822L;
	private static final int NUM_OF_CHILDREN = 16;
	private static final int MANDELBROT_LIMIT = 2;
	private double lowerX;
	private double lowerY;
	private double edgeLength;
	private int n;
	private int iterLimit;
	private int taskSize;
	private int chunkLocationX;
	private int chunkLocationY;
	
	public MandelbrotSetTask(double lowerX, double lowerY, double edgeLength,
			int n, int iterLimit) {
		super(DEFAULT_TASK_ID, DEFAULT_TASK_ID, Task.Status.DECOMPOSE, System
				.currentTimeMillis());
		this.lowerX = lowerX;
		this.lowerY = lowerY;
		this.edgeLength = edgeLength;
		this.n = n;
		this.iterLimit = iterLimit;
		this.taskSize = new Double(Math.sqrt(n * n / NUM_OF_CHILDREN)).intValue();
	}

	private MandelbrotSetTask(double lowerX, double lowerY, double edgeLength,
			int n, int iterLimit, int chunkLocationX,int chunkLocationY, Task.Status s, String taskId, String parentId) {
		this(lowerX, lowerY, edgeLength, n, iterLimit);
		this.chunkLocationX=chunkLocationX;
		this.chunkLocationY=chunkLocationY;
		super.init(s, taskId, parentId);
	}

	@Override
	public Result<MandelbrotSetChunk> decompose() {
		if (this.getId().equals(DEFAULT_TASK_ID)) {
			List<Task<MandelbrotSetChunk>> subTasks = this.chopMandelbrotTask();
			return new ResultImpl<MandelbrotSetChunk>(this.getStartTime(),
					System.currentTimeMillis(), subTasks);
		}
		else{
			MandelbrotSetChunk value=this.computeMandelbrotSet();
			return new ResultImpl<MandelbrotSetChunk>(this.getStartTime(),
					System.currentTimeMillis(), value);
		}
		
	}

	private MandelbrotSetChunk computeMandelbrotSet() {
		int[][] values = new int[n][n];
		int i = 0, j = 0;
		for (double xIndex = this.lowerX; i<n; xIndex += edgeLength, i++) {
			j = 0;
			
			for (double yIndex = this.lowerY; j<n; yIndex += edgeLength, j++) {
				double zLowerReal = xIndex;
				double zLowerComplex = yIndex;
				double zReal = zLowerReal;
				double zComplex = zLowerComplex;

				int k;
				for (k = 0; k < this.iterLimit
						&& (modulus(zReal,zComplex) <= MandelbrotSetTask.MANDELBROT_LIMIT); k++) {
					double zPrevReal = zReal;
					zReal = zReal * zReal - zComplex * zComplex + zLowerReal;
					zComplex = 2 * zPrevReal * zComplex + zLowerComplex;
				}

				if (modulus(zReal,zComplex) <= MandelbrotSetTask.MANDELBROT_LIMIT) {

					values[i][j] = this.iterLimit;
				} else {

					values[i][j] = k;
				}
			}
		}
		
		return new MandelbrotSetChunk(this.chunkLocationX, this.chunkLocationY, values);
		
	}

	private List<Task<MandelbrotSetChunk>> chopMandelbrotTask(){
		int i = 0, j = 0;
		double jump = edgeLength / n;
		List<String> childIds = this.getChildIds();
		int childIdIndex = 0;
		List<Task<MandelbrotSetChunk>> subTasks = new Vector<Task<MandelbrotSetChunk>>();
		for (double xIndex = this.lowerX; i < n; xIndex += jump
				* this.taskSize, i += this.taskSize) {
			j = 0;
			for (double yIndex = this.lowerY; j < n; yIndex += jump
					* this.taskSize, j += this.taskSize) {
				Task<MandelbrotSetChunk> aMandelbrotSetTask = new MandelbrotSetTask(
						xIndex, yIndex, jump, this.taskSize, iterLimit,i,j,
						Task.Status.DECOMPOSE, childIds.get(childIdIndex),
						this.getId());
				subTasks.add(aMandelbrotSetTask);
				childIdIndex++;
			}
		}
		return subTasks;
	}
	
	
	
	@Override
	public Result<MandelbrotSetChunk> compose(List<?> list) {
		List<MandelbrotSetChunk> listOfChunks=(List<MandelbrotSetChunk>)list;
		int[][] allValues=new int[this.n][this.n];
		
		for(MandelbrotSetChunk chunk : listOfChunks){
			int[][] values = chunk.getValues();
			int startX=chunk.getX();
			int startY=chunk.getY();
			for (int valuesRow = 0; valuesRow < values.length; valuesRow++) {
				for (int valuesCol = 0; valuesCol < values[0].length; valuesCol++) {
					int actualX = new Double(valuesRow + startX).intValue();
					int actualY = new Double(n - 1 - (valuesCol + startY))
							.intValue();
					allValues[actualX][actualY] = values[valuesRow][valuesCol];
				}
			}
		}
		
		MandelbrotSetChunk finalResult=new MandelbrotSetChunk(this.chunkLocationX, this.chunkLocationY, allValues);
		return new ResultImpl<MandelbrotSetChunk>(this.getStartTime(),
				System.currentTimeMillis(), finalResult);
		
	}

	@Override
	public int getDecompositionSize() {
		return NUM_OF_CHILDREN;
	}
	
	private double modulus(double zReal, double zComplex){
		return Math.sqrt(zReal * zReal + zComplex * zComplex);
	}
	
	

}