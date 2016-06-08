package Program;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Arrays;
import java.util.Scanner;

import Stock.data.DBHandler;
import Stock.modul.StockReport;
import Stock.modul.SuppliersModule;
import Stock.market.Item;
import Stock.market.Manufacturer;
import Stock.supplierManagement.Agreement;
import Stock.supplierManagement.CataloguedItem;
import Stock.supplierManagement.DiscountTerm;
import Stock.supplierManagement.Supplier;
import Stock.supplierManagement.SupplierCard;
import Stock.supplierManagement.SupplierContact;
import Stock.supplyOrder.ItemPricing;

public class Suppliers {

	private static final boolean ON_UI_CHANGE_CLEAR = false;

	private static Scanner in;
	private static SuppliersModule logic;
	private static int clearence;
	private static final int ADMIN = 1;
	public static void run(int cl) 
	{
		clearence = cl;
		in = new Scanner(System.in);
		logic = new SuppliersModule();

		mainScreen();
	}

	private static boolean isNumeric(String str)
	{
		NumberFormat formatter = NumberFormat.getInstance();
		ParsePosition pos = new ParsePosition(0);
		formatter.parse(str, pos);
		return str.length() == pos.getIndex();
	}

	private static boolean isBoolean(String str) {
		return (str.toLowerCase().equals("f") ||
				str.toLowerCase().equals("false") ||
				str.toLowerCase().equals("t") ||
				str.toLowerCase().equals("true"));
	}

	private static void uiSeperator() {
		if (ON_UI_CHANGE_CLEAR)
			try {
				Runtime.getRuntime().exec("cls");
			} catch (IOException e) {
				// shouldn't happen
				System.out.println("=============================================");
			}
		else
			System.out.println("=============================================");
	}


	/**
	 * a utility method for converting potential index input into textual input
	 * for example, options:                      1. option1    2. option2
	 * enables user input '1' and translates it to 'option1'
	 * @param in : the users input
	 * @param arr : the options
	 * @return : a usable String
	 */
	private static String convert(String in, String[] arr) {
		if (isNumeric(in) && Integer.valueOf(in) >= 1 && Integer.valueOf(in) <= arr.length)
			return arr[Integer.valueOf(in) - 1];
		else return in;
	}

	private static CataloguedItem selectCatalougeItemScreen(Supplier s) {
		uiSeperator();
		System.out.println("Select catalouge item to add");
		CataloguedItem[] ci = logic.getSuppliersCatalougeItems(s.getCard().getCardNumber());
		for (int i = 0; i < ci.length; i++) {
			System.out.println((i+1) + ". " + ci[i].getItem().getItemName() + " | " +
					ci[i].getItem().getManufacturer().getManufacturerName());
		}
		String input = in.nextLine();
		if (isNumeric(input) && Integer.valueOf(input) > 0 && Integer.valueOf(input) <= ci.length)
			return ci[Integer.valueOf(input)-1];
		return null;

	}



	private static void mainScreen() {
		uiSeperator();
		System.out.println("Welcome to Super-Lee Supplier Module!");
		System.out.println();
		System.out.println("1. Manufacturers");
		System.out.println("2. Suppliers");
		System.out.println("3. Enter stock report");
		System.out.println("4. Return to startup menu");

		String s = in.nextLine();
		if (s.equals("1") || s.equals("Manufacturers")) {
			manufacturersScreen(null);

		} else if (s.equals("2") || s.equals("Suppliers")) {
			suppliersScreen();

		} else if (s.equals("3")) {
			stockReportEntryScreen();

		} else if (s.equals("4")) {
			return;
		} else if (s.equals("-h") || s.equals("help")) {
			System.out.println("Enter menu name or id to navigate");
			System.out.println("-ex : exit");
			mainScreen();

		} else if (s.equals("exit") || s.equals("-ex")) {
			System.exit(0);

		} else {
			System.out.println("Unrecognized command, please retry or use -h for help");
			mainScreen();

		}
	}

	private static void manufacturersScreen(Supplier sForCatalougeAdding) {
		uiSeperator();
		System.out.println("Manufacturers: ");

		Manufacturer[] man = logic.getManufacturers();
		String[] manString = new String[man.length];
		for (int i = 0; i < man.length; i++) {
			System.out.println((i + 1) + ". " + man[i].getManufacturerName());
			manString[i] = man[i].getManufacturerName();
		}

		String s = in.nextLine();
		if (s.equals("-b") || s.equals("back")) {
			mainScreen();

		} else if (s.equals("-h") || s.equals("help")) {
			System.out.println("-b : go back");
			System.out.println("[id] : view/select manufacturer");
			System.out.println("-a [id] : add manufacturer");
			System.out.println("-r [id] : remove manufacturer");
			manufacturersScreen(sForCatalougeAdding);

		} else if (s.equals("exit") || s.equals("-ex")) {
			System.exit(0);

		} else {// View manufacturer
			if (Arrays.asList(manString).contains(convert(s, manString))) {
				manufacturerScreen(convert(s, manString), sForCatalougeAdding);
			} else if (clearence >= ADMIN) {
				String[] arr = s.split(" ");
				if (arr[0].equals("-a")) {
					if (arr.length > 1)
						logic.addManufacturer(new Manufacturer(convert(arr[1], manString)));
					manufacturersScreen(sForCatalougeAdding);

				} else if (arr[0].equals("-r")) {
					if (arr.length > 1)
						logic.removeManufacturer(new Manufacturer(convert(arr[1], manString)));
					manufacturersScreen(sForCatalougeAdding);

				} else {
					System.out.println("Unrecognized command, please retry or use -h for help");
					manufacturersScreen(sForCatalougeAdding);

				}
			} else {
				System.out.println("You don't have the requiered autherization.");
				manufacturersScreen(sForCatalougeAdding);
			}

		}
	}

	private static void manufacturerScreen(String name, Supplier sForCatalougeAdding) {
		uiSeperator();
		if (sForCatalougeAdding != null)
			System.out.println("Add catalouge item for supplier " + sForCatalougeAdding.getCard().getSupplierName());
		System.out.println("Manufacturer: " + name);
		Item[] items = logic.getItems(name);
		String[] itemsString = new String[items.length];
		for (int i = 0; i < items.length; i++) {
			System.out.println((i+1) + ". " + items[i].getItemName());
			itemsString[i] = items[i].getItemName();
		}

		String input;
		String s = input = in.nextLine();
		if (s.equals("-b") || s.equals("back")) {
			manufacturersScreen(sForCatalougeAdding);

		} else if (s.equals("-h") || s.equals("help")) {
			System.out.println("-b : go back");
			System.out.println("-a [id] : add item");
			System.out.println("-e [id] [newName] : edit item");
			System.out.println("-r [id] : remove item");
			if (sForCatalougeAdding != null)
				System.out.println("[id] [catalougeNum] : add catalouge item for supplier");
			manufacturerScreen(name, sForCatalougeAdding);
		} else if (s.equals("exit") || s.equals("-ex")) {
			System.exit(0);

		} else {
			String[] arr = input.split(" ");
			if (sForCatalougeAdding != null) {
				if (arr.length > 1 && isNumeric(arr[0]) && isNumeric(arr[1]) && Integer.valueOf(arr[0]) > 0 && Integer.valueOf(arr[0]) <= items.length) {
					CataloguedItem ci = new CataloguedItem(items[Integer.valueOf(arr[0]) - 1], Integer.valueOf(arr[1]), sForCatalougeAdding);
					logic.addCatalougeItem(ci);
					// Didn't forget to update screen
					// This ui is called for adding catalouge items
				} else {
					System.out.println("Formate not supported! expected '[id] [catalougeNum]'");
				}
			} else if (clearence >= ADMIN) {
				if (arr[0].equals("-a")) {
					if (arr.length > 1)
						logic.addItem(new Item(arr[1], new Manufacturer(name)));
					manufacturerScreen(name, sForCatalougeAdding);

				} else if (arr[0].equals("-e")) {
					if (arr.length > 2)
						logic.changeItemName(items[Integer.valueOf(arr[1]) - 1], arr[2]);
					else
						System.out.println("Formate not supported! expected '-e [id] [newName]'");
					manufacturerScreen(name, sForCatalougeAdding);

				} else if (arr[0].equals("-r")) {
					if (arr.length > 1)
						logic.removeItem(new Item(convert(arr[1], itemsString), new Manufacturer(name)));
					manufacturerScreen(name, sForCatalougeAdding);

				} else {
					System.out.println("Unrecognized command, please retry or use -h for help");
					manufacturerScreen(name, sForCatalougeAdding);

				}
			} else {
				System.out.println("You don't have the requiered autherization.");
				manufacturerScreen(name, sForCatalougeAdding);
			}
		}
	}

	private static void suppliersScreen() {
		uiSeperator();
		System.out.println("Suppliers: ");

		Supplier[] sup = logic.getSuppliers();
		String[] supString = new String[sup.length];
		for (int i = 0; i < sup.length; i++) {
			System.out.println((i+1) + ". " + sup[i].getCard().getSupplierName());
			supString[i] = sup[i].getCard().getSupplierName();
		}

		String input;
		String s1 = input = in.nextLine();
		if (s1.equals("-b") || s1.equals("back")) {
			mainScreen();

		} else if (s1.equals("-h") || s1.equals("help")) {
			System.out.println("-b : go back");
			System.out.println("[id] : view supplier");
			System.out.println("-a [serialNum] [address] [bankNum] [paymentTerms]: add supplier");
			System.out.println("-d [id] : details of supplier");
			System.out.println("-e [id] : edit suplier. optional flags: '-na [name]', '-bn [backNumber]', '-pt [paymentTerms]'");
			System.out.println("-r [id] : remove supplier");
			suppliersScreen();

		} else if (s1.equals("exit") || s1.equals("-ex")) {
			System.exit(0);

		} else {
			if (Arrays.asList(supString).contains(convert(input, supString))) {
				supplierScreen(sup, Integer.valueOf(input) - 1);
			} else if (clearence >= ADMIN) {
				String[] arr = input.split(" ");
				if (arr[0].equals("-a")) {
					if (arr.length > 5 &&
							(arr[5].toLowerCase().equals("m") || arr[5].toLowerCase().equals("monthly") ||
									arr[5].toLowerCase().equals("y") || arr[5].toLowerCase().equals("yearly"))) {
						SupplierCard sc = new SupplierCard(arr[1],
								Integer.valueOf(arr[2]),
								arr[3],
								Integer.valueOf(arr[4]),
								((arr[5].toLowerCase().equals("m") || arr[5].toLowerCase().equals("monthly")) ?
										SupplierCard.PaymentTerm.Monthly : SupplierCard.PaymentTerm.Yearly),
								null);
						logic.addSupplier(new Supplier(sc));
					} else {
						System.out.println("wrong args");
						System.out.println("-a [id] [serialNum] [address] [bankNum] [paymentTerms]: add supplier");
					}
					suppliersScreen();

				} else if (arr[0].equals("-d")) {
					if (arr.length > 1 && Integer.valueOf(arr[1]) > 0 && Integer.valueOf(arr[1]) <= sup.length) {
						System.out.println(sup[Integer.valueOf(arr[1]) - 1]);
					} else {
						System.out.println("bad input");
					}
					suppliersScreen();

				} else if (arr[0].equals("-e")) {
					if (arr.length > 1) {
						Supplier s = sup[Integer.valueOf(arr[1]) - 1];
						boolean ok = true;
						for (int i = 2; i < arr.length; i++) {
							if (arr[i].equals("-na")) {
								s.getCard().setName(arr[++i]);

							} else if (arr[i].equals("-bn")) {
								s.getCard().setBankAccount(Integer.valueOf(arr[++i]));

							} else if (arr[i].equals("-pt")) {
								i++;
								if (!(arr[i].toLowerCase().equals("m") || arr[i].toLowerCase().equals("monthly") ||
										arr[i].toLowerCase().equals("y") || arr[i].toLowerCase().equals("yearly"))) {
									System.out.println("bad input");
									ok = false;
								} else {
									s.getCard().setPaymentTerms(((arr[i].toLowerCase().equals("m") || arr[i].toLowerCase().equals("monthly")) ?
											SupplierCard.PaymentTerm.Monthly : SupplierCard.PaymentTerm.Yearly));
								}

							}
						}
						if (ok) {
							logic.changeSupplier(s);
						}
					}
					suppliersScreen();

				} else if (arr[0].equals("-r")) {
					if (arr.length > 1) {
						if (isNumeric(arr[1]))
							logic.removeSupplier(sup[Integer.valueOf(arr[1]) - 1]);
						else {
							System.out.println("wrong args");
							System.out.println("-a [id] : remove supplier. [id] must be numeric!");
						}
					}
					suppliersScreen();

				} else {
					System.out.println("Unrecognized command, please retry or use -h for help");
					suppliersScreen();

				}
			} else {
				System.out.println("You don't have the requiered autherization.");
				suppliersScreen();
			}

		}
	}

	private static void supplierScreen(Supplier[] sup, int id) {
		uiSeperator();
		System.out.println("Supplier: " + sup[id].getCard().getCardNumber() + " | " + sup[id].getCard().getSupplierName());
		System.out.println("1. Agreements");
		System.out.println("2. Items");
		System.out.println("3. Contacts");

		String s = in.nextLine().toLowerCase();
		if (s.equals("1") || s.equals("agreements")) {
			supplierAgreementsScreen(sup, id);

		} else if (s.equals("2") || s.equals("items")) {
			supplierCatalougeItemsScreen(sup, id);

		} else if (s.equals("3") || s.equals("contacts")) {
			supplierContactsScreen(sup, id);

		} else if (s.equals("-b") || s.equals("back")) {
			suppliersScreen();

		} else if (s.equals("-h") || s.equals("help")) {
			System.out.println("Enter menu name or id to navigate");
			supplierScreen(sup, id);

		} else if (s.equals("exit") || s.equals("-ex")) {
			System.exit(0);

		} else {
			System.out.println("Unrecognized command, please retry or use -h for help");
			supplierScreen(sup, id);

		}
	}

	private static void supplierContactsScreen(Supplier[] sup, int id) {
		uiSeperator();
		System.out.println("Contacts of: " + sup[id].getCard().getCardNumber() + " | " + sup[id].getCard().getSupplierName());

		SupplierContact[] con = logic.getContacts(sup[id]);
		String[] conString = new String[con.length * 2];
		for (int i = 0; i < con.length; i++) {
			System.out.println((i+1) + ". " + con[i].getName() + " | " + con[i].getEmail());
			conString[2 * i] = con[i].getName();
			conString[2 * i + 1] = con[i].getEmail();
		}

		String input;
		String s = input = in.nextLine();
		if (s.equals("-b") || s.equals("back")) {
			supplierScreen(sup, id);

		} else if (s.equals("-h") || s.equals("help")) {
			System.out.println("-b : go back");
			System.out.println("[id] : view contact");
			System.out.println("-a [name] [email] : add contact");
			System.out.println("-r [id] : remove contact");
			supplierContactsScreen(sup, id);

		} else if (s.equals("exit") || s.equals("-ex")) {
			System.exit(0);

		} else {
			if (isNumeric(input) && Integer.valueOf(input) > 0 && Integer.valueOf(input) <= con.length) {
				supplierContactPhoneNumbers(sup, id, con, Integer.valueOf(input) - 1);
			} else if (clearence >= ADMIN) {
				String[] arr = input.split(" ");
				if (arr[0].equals("-a")) {
					if (arr.length > 2) {
						SupplierContact sc = new SupplierContact(arr[1], arr[2]);
						logic.addContact(sup[id], sc);
					} else {
						System.out.println("wrong args");
						System.out.println("-a [name] [email] : add contact");
					}
					supplierContactsScreen(sup, id);

				} else if (arr[0].equals("-r")) {
					if (arr.length > 1) {
						if (isNumeric(arr[1]))
							logic.removeContact(sup[id], con[Integer.valueOf(arr[1]) - 1]);
						else {
							System.out.println("wrong args");
							System.out.println("-a [id] : remove supplier. [id] must be numeric!");
						}
					}
					supplierContactsScreen(sup, id);

				} else {
					System.out.println("Unrecognized command, please retry or use -h for help");
					supplierContactsScreen(sup, id);

				}
			} else {
				System.out.println("You don't have the requiered autherization.");
				supplierContactsScreen(sup, id);
			}

		}
	}

	private static void supplierContactPhoneNumbers(Supplier[] s, int sid, SupplierContact[] sc, int id) {
		uiSeperator();
		System.out.println("Contact " + sc[id].getEmail());
		String[] phones = logic.getContactsPhones(sc[id].getEmail());
		for (int i = 0; i < phones.length; i++) {
			System.out.println((i+1) + ". " + phones[i]);
		}

		String input;
		String s1 = input = in.nextLine();
		if (s1.equals("-b") || s1.equals("back")) {
			supplierContactsScreen(s, sid);

		} else if (s1.equals("-h") || s1.equals("help")) {
			System.out.println("-b : go back");
			System.out.println("-a [phone] : add phone");
			System.out.println("-r [id] : remove phone");
			supplierContactPhoneNumbers(s, sid, sc, id);

		} else if (s1.equals("exit") || s1.equals("-ex")) {
			System.exit(0);

		} else {
			if (clearence >= ADMIN) {
				String[] arr = input.split(" ");
				if (arr[0].equals("-a")) {
					if (arr.length > 1 && arr[1].length() == 10 && isNumeric(arr[1])) {
						logic.addContactPhoneNumber(sc[id], arr[1]);
					} else {
						System.out.println(
								"Format not supported. expected '-a [phone]' when [phone] is a 10 char numberic value");
					}
					supplierContactPhoneNumbers(s, sid, sc, id);

				} else if (arr[0].equals("-r")) {
					if (arr.length > 1 && phones.length >= Integer.valueOf(arr[1]) && Integer.valueOf(arr[1]) > 0)
						logic.removeContactPhoneNumber(sc[id], phones[Integer.valueOf(arr[1]) - 1]);
					supplierContactPhoneNumbers(s, sid, sc, id);

				} else {
					System.out.println("Unrecognized command, please retry or use -h for help");
					supplierContactPhoneNumbers(s, sid, sc, id);

				}
			} else {
				System.out.println("You don't have the requiered autherization.");
				supplierContactPhoneNumbers(s, sid, sc, id);
			}

		}
	}

	private static void supplierCatalougeItemsScreen(Supplier[] sup, int id) {
		uiSeperator();
		System.out.println("Supplier: " + sup[id].getCard().getCardNumber() + " | " + sup[id].getCard().getSupplierName());

		CataloguedItem[] items = logic.getSuppliersCatalougeItems(sup[id].getCard().getCardNumber());
		for (int i = 0; i < items.length; i++) {
			System.out.println((i+1) + ". " + items[i].getItem().getItemName() + " | " +
					items[i].getItem().getManufacturer().getManufacturerName() + " | " + 
					items[i].getCatalogueNumber());
			items[i].setSupplier(sup[id]);
		}

		String input;
		String s = input = in.nextLine();
		if (s.equals("-b") || s.equals("back")) {
			supplierScreen(sup, id);

		} else if (s.equals("-h") || s.equals("help")) {
			System.out.println("-b : go back");
			System.out.println("-a : add catalouge item");
			System.out.println("-r [id] : remove item");
			supplierCatalougeItemsScreen(sup, id);

		} else if (s.equals("exit") || s.equals("-ex")) {
			System.exit(0);

		} else {
			if (clearence >= ADMIN) {
				String[] arr = input.split(" ");
				if (arr[0].equals("-a")) {
					manufacturersScreen(sup[id]);
					supplierCatalougeItemsScreen(sup, id);

				} else if (arr[0].equals("-r")) {
					if (arr.length > 1) {
						if (isNumeric(arr[1]))
							logic.removeCatalougeItem(items[Integer.valueOf(arr[1]) - 1]);
						else {
							System.out.println("wrong args");
							System.out.println("-a [id] : remove supplier. [id] must be numeric!");
						}
					}
					supplierCatalougeItemsScreen(sup, id);

				} else {
					System.out.println("Unrecognized command, please retry or use -h for help");
					supplierCatalougeItemsScreen(sup, id);

				}
			} else {
				System.out.println("You don't have the requiered autherization.");
				supplierCatalougeItemsScreen(sup, id);
			}

		}
	}

	private static void supplierAgreementsScreen(Supplier[] sup, int id) {
		uiSeperator();
		System.out.println("Supplier: " + sup[id].getCard().getCardNumber() + " | " + sup[id].getCard().getSupplierName());
		Agreement[] agr = logic.getAgreements(sup[id].getCard().getCardNumber());
		for (int i = 0; i < agr.length; i++) {
			System.out.println((i+1) + ". Agreement " + agr[i].getAgreementId() + ". Delivery by " + (agr[i].isTransportationBySupplier() ? "supplier" : "us"));
		}


		String input;
		String s = input = in.nextLine();
		if (s.equals("-b") || s.equals("back")) {
			supplierScreen(sup, id);

		} else if (s.equals("-h") || s.equals("help")) {
			System.out.println("-b : go back");
			System.out.println("-a [isDeliveredBySupplier] : add agreement");
			System.out.println("-e [id] [isDeliveredBySupplier] : edit agreement deliver specs");
			System.out.println("-r [id] : remove agreement");
			supplierAgreementsScreen(sup, id);

		} else if (s.equals("exit") || s.equals("-ex")) {
			System.exit(0);

		} else {
			String[] arr = input.split(" ");
			if (isNumeric(arr[0]) && Integer.valueOf(arr[0]) > 0 && Integer.valueOf(arr[0]) <= agr.length) {
				agreementScreen(sup, id, agr, Integer.valueOf(arr[0]) - 1);
			} else if (clearence >= ADMIN) {
				if (arr[0].equals("-a")) {
					if (arr.length > 1 && isBoolean(arr[1]))
						logic.addAgreement(sup[id], new Agreement(0, sup[id], null, false,
								(arr[1].toLowerCase().equals("t") || arr[1].toLowerCase().equals("true)"))));
					else System.out.println("Unsupported format. expected: '-a [isDeliveredBySupplier]");
					supplierAgreementsScreen(sup, id);

				} else if (arr[0].equals("-e")) {
					if (arr.length > 2 && isBoolean(arr[2]))
						logic.changeAgreementDeliverySpecs(agr[Integer.valueOf(arr[1]) - 1], (arr[2].equals("t") || arr[2].equals("true")));
					supplierAgreementsScreen(sup, id);

				} else if (arr[0].equals("-r")) {
					if (arr.length > 1)
						logic.removeAgreement(agr[Integer.valueOf(arr[1]) - 1].getAgreementId());
					supplierAgreementsScreen(sup, id);

				} else {
					System.out.println("Unrecognized command, please retry or use -h for help");
					supplierAgreementsScreen(sup, id);

				}
			} else {
				System.out.println("You don't have the requiered autherization.");
				supplierAgreementsScreen(sup, id);
			}
		}
	}

	private static void agreementScreen(Supplier[] s, int sid, Agreement[] agr, int id) {
		uiSeperator();
		System.out.println("Agreement: " + agr[id].getAgreementId() + " | " + s[sid].getSupplierName());
		System.out.println("1. Days");
		System.out.println("2. Items");

		String s1 = in.nextLine().toLowerCase();
		if (s1.equals("1") || s1.equals("days")) {
			agreementSupplyDay(s, sid, agr, id);

		} else if (s1.equals("2") || s1.equals("items")) {
			agreementItemsScreen(s, sid, agr, id);

		} else if (s1.equals("-b") || s1.equals("back")) {
			supplierAgreementsScreen(s, sid);

		} else if (s1.equals("-h") || s1.equals("help")) {
			System.out.println("Enter menu name or id to navigate");
			agreementScreen(s, sid, agr, id);

		} else if (s1.equals("exit") || s1.equals("-ex")) {
			System.exit(0);

		} else {
			System.out.println("Unrecognized command, please retry or use -h for help");
			agreementScreen(s, sid, agr, id);

		}

	}

	private static void agreementItemsScreen(Supplier[] s, int sid, Agreement[] agr, int id) {
		uiSeperator();
		System.out.println("Agreement " + agr[id].getAgreementId());
		ItemPricing[] ci = logic.getAgreementsCatalougeItems(agr[id].getAgreementId());
		for (int i = 0; i < ci.length; i++) {
			System.out.println((i+1) + ". " + ci[i].getCatalougeItem().getItem().getItemName() + " | " +
					ci[i].getCatalougeItem().getItem().getManufacturer().getManufacturerName() + " | " + 
					ci[i].getPrice());
		}

		String input;
		String s1 = input = in.nextLine();
		if (s1.equals("-b") || s1.equals("back")) {
			agreementScreen(s, sid, agr, id);

		} else if (s1.equals("-h") || s1.equals("help")) {
			System.out.println("-b : go back");
			System.out.println("-a [id] : add item");
			System.out.println("-d [id] [quatity] [discount] : add/modify qds discount (set discount at 0 to delete)");
			System.out.println("-r [id] : remove item");
			agreementItemsScreen(s, sid, agr, id);

		} else if (s1.equals("exit") || s1.equals("-ex")) {
			System.exit(0);

		} else {
			if (isNumeric(input) && Integer.valueOf(input) > 0 && Integer.valueOf(input) <= ci.length) {
				discountsScreen(s, sid, agr, id, ci, Integer.valueOf(input) - 1);
			} else if (clearence >= ADMIN) {
				String[] arr = input.split(" ");
				if (arr[0].equals("-a")) {
					CataloguedItem catitem = selectCatalougeItemScreen(s[sid]);
					if (catitem != null) {
						System.out.println("=============================================");
						System.out.println("Please enter price: ");
						double price = in.nextDouble();
						in.nextLine();
						logic.addItemToAgreement(agr[id], catitem, price);
					}
					agreementItemsScreen(s, sid, agr, id);

				} else if (arr[0].equals("-d")) {
					if (arr.length > 3) {
						logic.addDiscountToQDS(agr[id], ci[Integer.valueOf(arr[1]) - 1].getCatalougeItem(),
								Integer.valueOf(arr[2]), Double.valueOf(arr[3]));
					} else
						System.out.println("Formate not supported! expected '-d [id] [quantity] [discount]'");
					agreementItemsScreen(s, sid, agr, id);

				} else if (arr[0].equals("-r")) {
					if (arr.length > 1 && agr.length >= Integer.valueOf(arr[1]) && Integer.valueOf(arr[1]) > 0)
						logic.removeItemFromAgreement(agr[id], ci[Integer.valueOf(arr[1]) - 1].getCatalougeItem().getCatalogueNumber());
					agreementItemsScreen(s, sid, agr, id);

				} else {
					System.out.println("Unrecognized command, please retry or use -h for help");
					agreementItemsScreen(s, sid, agr, id);

				}
			} else {
				System.out.println("You don't have the requiered autherization.");
				agreementItemsScreen(s, sid, agr, id);
			}
		}
	}
	
	private static void discountsScreen(Supplier[] s, int sid, Agreement[] agr, int aid, ItemPricing[] ip, int id) {
		uiSeperator();
		System.out.println("Agreement " + agr[aid].getAgreementId() + ", Catalouge item: " +
				ip[id].getCatalougeItem().getCatalogueNumber() + " | " + ip[id].getCatalougeItem().getItem().getItemName());
		DiscountTerm[] ci = logic.getAgreementsItemsDiscount(agr[aid].getAgreementId(), ip[id].getCatalougeItem().getCatalogueNumber());
		for (int i = 0; i < ci.length; i++) {
			System.out.println((i+1) + ". " + ci[i].getMinQuantity() + " : " +
					ci[i].getDiscount());
		}

		String input;
		String s1 = input = in.nextLine();
		if (s1.equals("-b") || s1.equals("back")) {
			agreementItemsScreen(s, sid, agr, aid);

		} else if (s1.equals("-h") || s1.equals("help")) {
			System.out.println("-b : go back");
			System.out.println("-a [quantity] [discount] : add discount");
			System.out.println("-r [id] : remove discount");
			discountsScreen(s, sid, agr, aid, ip, id);

		} else if (s1.equals("exit") || s1.equals("-ex")) {
			System.exit(0);

		} else {
			if (clearence >= ADMIN) {
				String[] arr = input.split(" ");
				if (arr[0].equals("-a")) {
					if (arr.length > 2) {
						for (int i = 0; i < ci.length; i++) {
							if (Integer.valueOf(arr[1]) == ci[i].getMinQuantity())
								logic.removeDiscountToQDS(agr[id], ip[id].getCatalougeItem(), ci[i].getMinQuantity(),
										ci[i].getDiscount());
						}
						logic.addDiscountToQDS(agr[aid], ip[id].getCatalougeItem(), Integer.valueOf(arr[1]),
								Double.valueOf(arr[2]));
					} else
						System.out.println("Formate not supported! expected '-a [quantity] [discount]'");
					discountsScreen(s, sid, agr, aid, ip, id);

				} else if (arr[0].equals("-r")) {
					if (arr.length > 1) {
						logic.removeDiscountToQDS(agr[aid], ip[id].getCatalougeItem(),
								ci[Integer.valueOf(arr[1]) - 1].getMinQuantity(),
								ci[Integer.valueOf(arr[1]) - 1].getDiscount());
					} else
						System.out.println("Formate not supported! expected '-d [id] [quantity] [discount]'");
					discountsScreen(s, sid, agr, aid, ip, id);

				} else {
					System.out.println("Unrecognized command, please retry or use -h for help");
					discountsScreen(s, sid, agr, aid, ip, id);

				}
			} else {
				System.out.println("You don't have the requiered autherization.");
				discountsScreen(s, sid, agr, aid, ip, id);
			}

		}
	}
	
	private static String getNumberWithSurffix(int num) {
		if (num == 1 || num == 21 || num == 31) return num + "st";
		if (num == 2 || num == 22) return num + "nd";
		if (num == 3 || num == 23) return num + "rd";
		return num + "th";
	}

	private static void agreementSupplyDay(Supplier[] s, int sid, Agreement[] agr, int id) {
		uiSeperator();
		System.out.println("Agreement " + agr[id].getAgreementId());
		Integer[] days = logic.getAgreementSupplyDays(agr[id].getAgreementId());
		for (int i = 0; i < days.length; i++) {
			System.out.println(getNumberWithSurffix(days[i]) + " of each month");
		}

		String input;
		String s1 = input = in.nextLine();
		if (s1.equals("-b") || s1.equals("back")) {
			agreementScreen(s, sid, agr, id);

		} else if (s1.equals("-h") || s1.equals("help")) {
			System.out.println("-b : go back");
			System.out.println("-a [day] : add day");
			System.out.println("-r [day] : remove day");
			agreementSupplyDay(s, sid, agr, id);

		} else if (s1.equals("exit") || s1.equals("-ex")) {
			System.exit(0);

		} else {
			if (clearence >= ADMIN) {
				String[] arr = input.split(" ");
				if (arr[0].equals("-a")) {
					if (arr.length > 1 && Integer.valueOf(arr[1]) > 0 && Integer.valueOf(arr[1]) < 32) {
						logic.addAgreementSupplyDay(agr[id], Integer.valueOf(arr[1]));
					}
					agreementSupplyDay(s, sid, agr, id);

				} else if (arr[0].equals("-r")) {
					if (arr.length > 1 && Integer.valueOf(arr[1]) > 0 && Integer.valueOf(arr[1]) < 32) {
						logic.removeDayFromAgreement(agr[id], Integer.valueOf(arr[1]));
					}
					agreementSupplyDay(s, sid, agr, id);

				} else {
					System.out.println("Unrecognized command, please retry or use -h for help");
					agreementSupplyDay(s, sid, agr, id);

				}
			} else {
				System.out.println("You don't have the requiered autherization.");
				agreementSupplyDay(s, sid, agr, id);
			}

		}
	}
	
	private static void stockReportEntryScreen() {
		uiSeperator();
		Item[] i = logic.getDetailedItems();
		for (int j = 0; j < i.length; j++) {
			System.out.println((j+1) + ". " + i[j].getItemName() + " | " + i[j].getManufacturer().getManufacturerName());
		}
		StockReport sr = new StockReport();
		System.out.println("Start spewing off ids. enter -1 to finish");
		boolean stillEntering = true;
		while(stillEntering) {
			String[] arr = in.nextLine().split(" ");			
			if (arr.length > 1 && isNumeric(arr[0]) && isNumeric(arr[1]) && 
					Integer.valueOf(arr[0]) > 0 && Integer.valueOf(arr[0]) <= i.length)
				sr.addItem(i[Integer.valueOf(arr[0]) - 1], Integer.valueOf(arr[1]));
			else if (arr[0].equals("-1")) stillEntering = false;
			else System.out.println("Unsupported formate! expected '[id] [quantity]'. enter [id]='-1' to finish");
		}
		try {
			new DBHandler().makeOrders(sr);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		mainScreen();
	}

	public static void datedOrder()
	{
		SuppliersModule sm = new SuppliersModule();
		sm.checkForTimedOrder();
	}
}