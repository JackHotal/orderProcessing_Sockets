package opclient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

public class Client {
    private static ObjectInputStream mObjIn;
    private static PrintStream mOut;
    private static Socket mSocket;
    public static final int mPORT = 1574;

    public static void main(String[] args) {
        PrintStream cout = System.out;
        Scanner cin = new Scanner(System.in);
        try {
            mSocket = new Socket("localhost", mPORT);//("128.227.246.18", mPORT);
            mObjIn = new ObjectInputStream(mSocket.getInputStream());
            mOut = new PrintStream(mSocket.getOutputStream());
            // now send login info


            cout.println("Please enter your password");

            int count = 0;
            String pass = cin.nextLine();

            mOut.println(String.format("login:ism6236:%s", pass));
            String resp = (String) mObjIn.readObject();

            cout.println(resp);
            mOut.println("getproductids:");
            List<String> aclist = (List<String>) mObjIn.readObject();
            for (String s : aclist) {
                //String p = db.getProductDetail(s);
                mOut.println(String.format("getproductdetail:%s", s));
                String p = (String) mObjIn.readObject();
                cout.println(String.format("%s --> %s", s, p));
            }
            cout.println();
            cout.print("Enter P to purchase,  L to list customer orders , Q to quit");
            cout.flush();
            String input = cin.nextLine();
            boolean quit = false;
            //ArrayList<String> ord = new ArrayList<String>();
            String ord="";
            while (!quit) {
                int c = input.charAt(0);
                switch (c) {
                    case 'p':
                    case 'P':
                        cout.print("Enter Customer No: ");
                        cout.flush();
                        String cno = cin.nextLine();
                        while (true) {
                            cout.print("Enter Product No: ");
                            String pid = cin.nextLine();
                            cout.print("Enter Quantity: ");
                            cout.flush();
                            String q = cin.nextLine();
                            cout.print("Enter Price: ");
                            cout.flush();
                            String price = cin.nextLine();
                            String oline = String.format("%s,0,%s,%s",  pid, price, q); //the GUI had inserted the description so I am inserting 0 as a place holder
                            ord += String.format("%s:", oline);
                            cout.print("Enter N to add another product, P to finalize the purchase ");
                            cout.flush();
                            String d = cin.nextLine();
                            if (d.toLowerCase().equals("p")) {
                                break;
                            }
                        }
                        //  int n = db.Purchase(cno, ord);
                        //Update(accno, amt);
                        mOut.println(String.format("purchase:%s:%s", cno, ord));
                        int n = (int) mObjIn.readObject();
                        cout.println(String.format("%d records got updated",n));
                        break;

                    case 'L':
                    case 'l':
                        cout.println("Enter Customer No: ");
                        cout.flush();
                        cno = cin.nextLine();
                        // List<String> l = (List<String>) db.getCustomerOrders(cno);
                        mOut.println(String.format("getcustomerorders:%s", cno));
                        List<String> l = (List<String>) mObjIn.readObject();

                        for (int i = 0; i < l.size(); i++) {
                            String oid = l.get(i);
                            // List<String> od = db.getOrderDetails(oid);
                            mOut.println(String.format("getorderdetails:%s", oid));
                            List<String> od = (List<String>) mObjIn.readObject();
                            cout.println(String.format("OrderId:%s", oid));
                            for (String s : od) {
                                cout.println(s);
                            }
                        }

                        break;
                    default:
                        quit = true;

                }

                if (!quit) {
                    cout.print("Enter P to purchase,  L to list customer orders , Q to quit");
                    cout.flush();
                    input = cin.nextLine();
                }


            }
            mOut.println("quit:");
            resp = (String) mObjIn.readObject();
            cout.println(resp);
        } catch (IOException e) {
            cout.println("Server not running, exiting");

            System.exit(-1);
        } catch (ClassNotFoundException cfe) {
            cout.println("Abnormal communication with the server, exiting");

            System.exit(-1);
        }
    }
}
