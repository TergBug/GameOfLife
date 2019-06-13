package terg.iinur;

import java.text.DecimalFormat;

public class App {
	public static void main(String[] args) throws Exception {
		DecimalFormat df = new DecimalFormat("#.##");
		double[][] input = {{1.0, 1.0, 1.0, 1.0}, {1.0, 1.0, 1.0, 0.0}, 
				{1.0, 1.0, 0.0, 1.0}, {1.0, 1.0, 0.0, 0.0},
				{1.0, 0.0, 1.0, 1.0}, {1.0, 0.0, 1.0, 0.0}, 
				{1.0, 0.0, 0.0, 1.0}, {1.0, 0.0, 0.0, 0.0},
				{0.0, 1.0, 1.0, 1.0}, {0.0, 1.0, 1.0, 0.0}, 
				{0.0, 1.0, 0.0, 1.0}, {0.0, 1.0, 0.0, 0.0},
				{0.0, 0.0, 1.0, 1.0}, {0.0, 0.0, 1.0, 0.0}, 
				{0.0, 0.0, 0.0, 1.0}, {0.0, 0.0, 0.0, 0.0}};
		double[][] ideal = {{0.0}, {1.0}, {0.0}, {1.0}, 
				{0.0}, {1.0}, {0.0}, {1.0}, 
				{0.0}, {1.0}, {0.0}, {1.0}, 
				{0.0}, {1.0}, {0.0}, {1.0}};
		Evol evol = new Evol(25, new Individ(null, new TrainSet(input, ideal), true) {

			@Override
			public double effect(double[] TrainSetArgs) {
				return 0;
			}
		});
		
		NN nn = evol.fullTrain();
		for(int i = 0; i<input.length; i++) {
			double[] get = nn.work(input[i]);
			for(int j = 0; j<get.length; j++) {
				System.out.println(df.format(input[i][j])+"->"+df.format(get[j]));
			}
			System.out.println("Error: "+df.format(nn.getError(get, input[i])));
		}
	}
}
