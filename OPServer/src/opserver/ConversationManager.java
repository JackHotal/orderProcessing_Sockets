package opserver;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import oplib.Order;


public class ConversationManager extends Thread {

    protected Socket mClientSocket;

    protected BufferedReader mIn;
    protected ObjectOutputStream mObjOut;

    protected Order mdb;

    public ConversationManager(Socket clientSocket) {
        mClientSocket = clientSocket;
        try {

            mIn = new BufferedReader(new InputStreamReader(mClientSocket.getInputStream()));
            mObjOut = new ObjectOutputStream(mClientSocket.getOutputStream());

        }
        catch (IOException e) {
            try {
                mClientSocket.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            e.printStackTrace();
            return;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run(){
        String line = "";
        while (true) {
            try {
                // get the line
                line = mIn.readLine();
                System.out.println(line);
                // parse the message (name,ccno,expdate,amount)
                String cname, accno, sql, amt;
                String[] p = line.split(":");
                cname = p[0];
                if (cname.compareTo("login") == 0) {
                    String uid = p[1];
                    String pass = p[2];
                    mdb = new Order(uid, pass);
                    String login = "Fail";
                    if (mdb.IsConnected()) {
                        login = "OK";
                    }
                    mObjOut.writeObject(login);
                } if (cname.compareTo("getcustomer") == 0) {

                    String cid = p[1];
                    String cdetail = mdb.getCustomer(cid);
                    mObjOut.writeObject(cdetail);

                } else if (cname.compareTo("purchase") == 0) {
                    //case "transact":
                    String cid = p[1];
                    ArrayList<String> a = new ArrayList<String>();
                    for (int j=2; j < p.length; j++)
                        a.add(p[j]);
                    int n = mdb.Purchase(cid,a);

                    mObjOut.writeObject(n);

                } else if (cname.compareTo("getcustomerorders") == 0) {
                    String cid = p[1];
                    List<String> l = mdb.getCustomerOrders(cid);
                    mObjOut.writeObject(l);
                } // break;
                else if (cname.compareTo("getorderdetails") == 0) {
                    String oid = p[1];
                    List<String> l = mdb.getOrderDetails(oid);
                    mObjOut.writeObject(l);
                }

                 else if (cname.compareTo("getproductids") ==0 ) {
                    List<String> l = mdb.getProductIds();
                    mObjOut.writeObject(l);
                } else if (cname.compareTo("getproductdetail") == 0) {
                    String pid = p[1];
                    String pdetail = mdb.getProductDetail(pid);
                    mObjOut.writeObject(pdetail);

                }else if (cname.compareTo("quit") == 0) {
                    mObjOut.writeObject("Closing connection ...");
                    mClientSocket.close();
                    break; //break the while loop
                }
            }
            catch (IOException e) {
                e.printStackTrace();
                break;
            }
        } //while
    }


}
