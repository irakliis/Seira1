package A11;
import java.util.Scanner;
import javax.swing.JOptionPane;
import java.awt.Font;
import java.io.PrintWriter;
import javax.swing.UIManager;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;


// HY252 - A1 - Exercise 1
public class DrawDelta {

	public static void main(String[] args) {
		char M;
		int L = 0;

		System.out.println("Please choose your mode.");
		System.out.println("c (Console),w (Window), f (File), g (Graphics) ");

		Scanner scanner = new Scanner(System.in);
		M = scanner.nextLine().charAt(0);

		// elegxos an oi times einai valid

		if (M==('c')||M==('w')||M==('f')) {
			if (M!='w') {
				System.out.println("Choose a number between 3 and 20.");
				L = scanner.nextInt();
				while(L>=3 && L<=20) {
					System.out.println("Valid mode and length, continuing the program.");
					drawDgraphics(L, M);
					System.out.println("Choose another number between 3 and 20.");
					L = scanner.nextInt();
				}
				System.out.println("Invalid range, closing program");
				scanner.close();
				return;
			}
			else {
				L = Integer.parseInt(JOptionPane.showInputDialog(
				"Give me a number ",5)); // το 5 είναι η default τιμή
				while(L>=3 && L<=20) {
					drawDgraphics(L, M);
					L = Integer.parseInt(JOptionPane.showInputDialog(
					"Give me a number ",5));
				}
			}
		}
		if(M=='g') {
			System.out.println("Showing graphics now!");
			drawDgraphics(L, M);
			scanner.close();
		} else {
			System.out.println("That is not a valid mode option, closing program.");
			scanner.close();
			return;
		}
	}


	static void drawDgraphics(int L,char M) {
		if (M=='c') {
			for (int row = 0; row < L; row++) {
				int width = 2 * row + 1;

				for (int space = 0; space < L - row - 1; space++) {
					System.out.print(" ");
				}

				for (int col = 0; col < width; col++) {
					if (row == L - 1 || col == 0 || col == width - 1) {
						System.out.print("*");
					} else {
						System.out.print(" ");
					}
				}
				System.out.println();
			}
		} else if (M=='w') {
			String output = "";

			for (int row = 0; row < L; row++) {
				int width = 2 * row + 1;

				for (int space = 0; space < L - row - 1; space++) {
					output += " ";
				}
				for (int col = 0; col < width; col++) {
					if (row == L - 1 || col == 0 || col == width - 1) {
						output += "*";
					} else {
						output += " ";
					}
				}
				output += "\n";
			}
			UIManager.put("OptionPane.messageFont",
			              new Font("Lucida Console", Font.BOLD, 20));

			JOptionPane.showMessageDialog(
			    null,
			    output,
			    "Παράθυρο Εξόδου",
			    JOptionPane.INFORMATION_MESSAGE
			);
		} else if(M=='f') {
			System.out.println("Ftiaxtike arxeio html me font = "+L);
			PrintWriter writer;
			try {
				writer = new PrintWriter("C:\\Users\\irakl\\Documents\\HY-252\\Files\\D.html");
				writer.println("<!DOCTYPE html>");
				writer.println("<html>");
				writer.println("<head>");
				writer.println("<meta http-equiv=\"content-type\" content=\"text/html;charset=utf-8\"/>");
				writer.println("</head>");
				writer.println("<body><font size=\"" +L +"\">D with font size =" + L +"</font></body>");
				writer.println("</html>");
				writer.close();
			} catch (Exception e) {
				System.out.println("Πρόβλημα: "+e);
			}
		} else {
			Frame f = new Frame("Ζωγραφίζοντας το Δ") {
				public void paint (Graphics g) {
					Graphics2D g2 = (Graphics2D) g;
					g2.draw(new Line2D.Double(50, 300, 200, 50)); // a line
					g2.draw(new Line2D.Double(200, 50, 350, 300));
					g2.draw(new Line2D.Double(50, 300, 350, 300));
				}
			};
			f.setSize(400,400);
			f.setVisible(true);
			f.repaint();
		}
	}
}