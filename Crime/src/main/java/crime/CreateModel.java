package crime;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.jena.graph.Triple;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.lang.PipedRDFIterator;
import org.apache.jena.riot.lang.PipedRDFStream;
import org.apache.jena.riot.lang.PipedTriplesStream;
import org.apache.jena.vocabulary.RDFS;
import org.apache.log4j.BasicConfigurator;
import com.esri.core.geometry.*;
import java.util.Comparator;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.SymmetricProperty;
import org.apache.jena.ontology.TransitiveProperty;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.vocabulary.XSD;

public class CreateModel {

    public static String ontologiesBase = "http://lab.Jena.Kdeg.ie/";

    public static String relationshipBase = "http://relationships.lab.Jena.Kdeg.ie/";

    public static String baseNs;

    public static String ontologyName = "CrimeOntology";

    public static OntModel ontology;

    public static ArrayList<Station> Stations = new ArrayList<>();
    public static ArrayList<Division> Divisions = new ArrayList<>();
    public static ArrayList<Crime> Crimes = new ArrayList<>();

    public static void main(String args[]) throws FileNotFoundException {

        baseNs = ontologiesBase + ontologyName + "#";
        ontology = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        BasicConfigurator.configure();

//County
        OntClass County = ontology.createClass(baseNs + "County");
        DatatypeProperty area = ontology.createDatatypeProperty(baseNs + "area");
        area.addLabel("area", null);
        area.addComment("Define area of a county", null);
        area.setDomain(County);
        area.setRange(XSD.xfloat);

        SymmetricProperty adjacentTo = ontology.createSymmetricProperty(baseNs + "adjacentTo");
        adjacentTo.addLabel("adjacentTo", null);
        adjacentTo.addComment("List other counties that is geographically touch with current county", null);
        adjacentTo.setDomain(County);
        adjacentTo.setRange(County);

        TransitiveProperty biggerThan = ontology.createTransitiveProperty(baseNs + "biggerThan");
        biggerThan.addLabel("biggerThan", null);
        biggerThan.addComment("List other counties that current county's area is bigger than", null);
        biggerThan.setDomain(County);
        biggerThan.setRange(County);

//Division 
        OntClass Division = ontology.createClass(baseNs + "Division");
        DatatypeProperty divisionName = ontology.createDatatypeProperty(baseNs + "DivisionName");
        divisionName.setDomain(Division);
        divisionName.setRange(XSD.xstring);

        DatatypeProperty inCounty = ontology.createDatatypeProperty(baseNs + "inCounty");
        inCounty.setDomain(Division);
        inCounty.setRange(County);
//Station

        OntClass Station = ontology.createClass(baseNs + "Station");
        DatatypeProperty stationId = ontology.createDatatypeProperty(baseNs + "StationId");
        stationId.setDomain(Station);
        stationId.setRange(XSD.nonNegativeInteger);

        DatatypeProperty stationName = ontology.createDatatypeProperty(baseNs + "StationName");
        stationName.setDomain(Station);
        stationName.setRange(XSD.xstring);

        DatatypeProperty inDivision = ontology.createDatatypeProperty(baseNs + "inDivision");
        inDivision.setDomain(Station);
        inDivision.setRange(Division);

//Crime        
        OntClass Murder = ontology.createClass(baseNs + "Attemps_or_threats_to_murder_assaults_harassments_and_related_offences");
        OntClass Robbery = ontology.createClass(baseNs + "Robbery_extortion_and_hijacking_offences");
        OntClass Government = ontology.createClass(baseNs + "Offences_against_government_justice_procedures_and_organisations_of_crime");
        OntClass Dangerous = ontology.createClass(baseNs + "Dangerous_or_negligent_acts");
        OntClass Drug = ontology.createClass(baseNs + "Controlled_drug_offences");
        OntClass Property = ontology.createClass(baseNs + "Damage_to_property_and_to_the_environment");
        OntClass Public = ontology.createClass(baseNs + "Public_order_and_other_social_code_offences");
        OntClass Theft = ontology.createClass(baseNs + "Theft_and_related_offences");
        OntClass Fraud = ontology.createClass(baseNs + "Fraud_Deception_and_related_offences");
        OntClass Wepons = ontology.createClass(baseNs + "Wepons_and_Explosives_Offences");
        OntClass Burglary = ontology.createClass(baseNs + "Burglary_and_related_offences");
        OntClass Kidnapping = ontology.createClass(baseNs + "Kidnapping_and_related_offences");

        RDFList crimeList = ontology.createList(new RDFNode[]{
            Murder,
            Robbery,
            Government,
            Dangerous,
            Drug,
            Property,
            Public,
            Theft,
            Wepons,
            Burglary,
            Kidnapping
        });
        OntClass Crime = ontology.createUnionClass(baseNs + "Crime", crimeList);

        Crime.addSubClass(Murder);
        Crime.addSubClass(Robbery);
        Crime.addSubClass(Government);
        Crime.addSubClass(Dangerous);
        Crime.addSubClass(Drug);
        Crime.addSubClass(Property);
        Crime.addSubClass(Public);
        Crime.addSubClass(Theft);
        Crime.addSubClass(Fraud);
        Crime.addSubClass(Wepons);
        Crime.addSubClass(Burglary);
        Crime.addSubClass(Kidnapping);

//
        OntClass Location = ontology.createClass(baseNs + "Location");
        DatatypeProperty X = ontology.createDatatypeProperty(baseNs + "X");
        DatatypeProperty Y = ontology.createDatatypeProperty(baseNs + "Y");
        X.setDomain(Location);
        X.setRange(XSD.xfloat);
        Y.setDomain(Location);
        Y.setRange(XSD.xfloat);

        //Location.addSuperClass(ontology.createCardinalityRestriction(null, X, 1));
        //Location.addSuperClass(ontology.createCardinalityRestriction(null, Y, 1));
        Station.setDisjointWith(Division);

        OntProperty hasX = ontology.createObjectProperty(baseNs + "has_X");
        OntProperty hasY = ontology.createObjectProperty(baseNs + "has_Y");

        Station.addProperty(hasX, "hasX");
        Station.addProperty(hasY, "hasY");

        ObjectProperty hasStations = ontology.createObjectProperty(baseNs + "hasStations");
        Division.addProperty(hasStations, "hasStations");
        hasStations.addInverseOf(inDivision);

        ObjectProperty hasDivisions = ontology.createObjectProperty(baseNs + "hasDivisions");
        County.addProperty(hasDivisions, "hasDivisions");
        hasDivisions.addInverseOf(inCounty);

        parser();

        Divisions.forEach((division) -> {
            Individual ind = ontology.createIndividual(baseNs + division.Name, Division);
        
        });

        Stations.remove(0);
        Stations.forEach((station) -> {
            Individual ind = ontology.createIndividual(baseNs + station.Name, Station);
            //ind.addProperty(hasX, "" + station.X);
            ind.addProperty(hasY, "" + station.Y);
            ind.addLiteral(hasX, ontology.createTypedLiteral(1.12, XSDDatatype.XSDnonNegativeInteger));
            System.out.println(station.Name);
            if(station.division != null)
                ind.addProperty(hasDivisions, station.division.Name);
        });

        Crimes.forEach((crime) -> {
            System.out.println(crime);
        });
        System.out.println("Done!");

        //INDIVIDUALS
        Model countyRDF = RDFDataMgr.loadModel("county.ttl");

        ArrayList<ArrayList<Object>> countyList = new ArrayList<>();
        ResIterator countyResIter = countyRDF.listResourcesWithProperty(RDFS.label);

        Property hasGeometry = countyRDF.getProperty("http://www.opengis.net/ont/geosparql#hasGeometry");
        Property asWKT = countyRDF.getProperty("http://www.opengis.net/ont/geosparql#asWKT");

        while (countyResIter.hasNext()) {
            Resource res = countyResIter.next();

            // labels
            NodeIterator labelsIter = countyRDF.listObjectsOfProperty(res, RDFS.label);
            List<RDFNode> labels = labelsIter.toList();
            String idLabel = "";
            String gaLabel = "";
            String enLabel = "";

            for (RDFNode label : labels) {
                Literal name = label.asLiteral();
                if (name.getLanguage().equals("ga")) {
                    gaLabel = name.getString();
                } else if (name.getLanguage().equals("en")) {
                    enLabel = name.getString();
                } else {
                    idLabel = name.getString();
                }
            }

            // WKT
            Resource geoResource = countyRDF.listObjectsOfProperty(res, hasGeometry).next().asResource();
            String wkt = countyRDF.listObjectsOfProperty(geoResource, asWKT).next().toString();
            wkt = wkt.substring(0, wkt.indexOf("^^"));
            OperatorImportFromWkt importer = OperatorImportFromWkt.local();
            Geometry geometry = importer.execute(WktImportFlags.wktImportDefaults, Geometry.Type.Unknown, wkt, null);

            ArrayList<Object> info = new ArrayList<>();
            info.add(idLabel);
            info.add(enLabel);
            info.add(gaLabel);
            info.add(geometry);
            // TODO: AREA unit correctness, add approximate scale for now. based on dublin
            float scale = 7365.0f;
            info.add((float) geometry.calculateArea2D() * scale);

            countyList.add(info);
        }

        countyList.sort(new Comparator<ArrayList<Object>>() {
            @Override
            public int compare(ArrayList<Object> o1, ArrayList<Object> o2) {
                float area1 = (float) o1.get(4);
                float area2 = (float) o2.get(4);
                return Float.compare(area2, area1);
            }
        });

        ArrayList<Individual> countyIndiList = new ArrayList<>();
        for (ArrayList<Object> info : countyList) {
            Individual aCounty = County.createIndividual(baseNs + info.get(0));
            aCounty.addLabel((String) info.get(0), null);
            aCounty.addLabel((String) info.get(1), "en");
            aCounty.addLabel((String) info.get(2), "ga");
            //aCounty.addLiteral(area, (float)info.get(4));

            countyIndiList.add(aCounty);
        }

        for (Individual countyIndi : countyIndiList) {
            int curIdx = countyIndiList.indexOf(countyIndi);
            Geometry curGeometry = (Geometry) countyList.get(curIdx).get(3);

            for (int i = curIdx + 1; i < countyIndiList.size(); i++) {
                if (i == curIdx) {
                    continue;
                }
                Individual otherCountyIndi = countyIndiList.get(i);
                if (i > curIdx) {
                    countyIndi.addProperty(biggerThan, otherCountyIndi);
                }
                Geometry otherGeometry = (Geometry) countyList.get(i).get(3);
                // TODO: Intersection not all correct, for instance, dublin
                OperatorIntersects intersects = OperatorIntersects.local();
                if (intersects.execute(curGeometry, otherGeometry, SpatialReference.create("WGS84"), null)) {
                    countyIndi.addProperty(adjacentTo, otherCountyIndi);
                }
            }
        }

        try {
            writeToFile(ontologyName);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            ReadModel.loadAllClassesOnt(ontologyName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static void parser() {

        String filename = "crime_rate_rdf.ttl";
        PipedRDFIterator<Triple> iter = new PipedRDFIterator<>();
        final PipedRDFStream<Triple> inputStream = new PipedTriplesStream(iter);

        // PipedRDFStream and PipedRDFIterator need to be on different threads
        ExecutorService executor = Executors.newSingleThreadExecutor();

        // Create a runnable for our parser thread
        Runnable parser = new Runnable() {

            @Override
            public void run() {
                // Call the parsing process.
                RDFDataMgr.parse(inputStream, filename);
            }
        };

        executor.submit(parser);

        String currentSubject = "";
        Division division = new Division();
        Station station = new Station();
        Crime crime = new Crime();

        while (iter.hasNext()) {
            Triple next = iter.next();

            //System.out.println("Subject:  " + next.getSubject());
            //System.out.println("Object:  " + next.getObject());
            //System.out.println("Predicate:  " + next.getPredicate());
            //System.out.println("\n");
            //System.out.println(currentSubject + " --- "+ next.getSubject());
            if (!next.getSubject().toString().equals(currentSubject)) {
                Divisions.add(division);
                Stations.add(station);
                Crimes.add(crime);
                division = new Division();
                station = new Station();
                crime = new Crime();
            }
            currentSubject = next.getSubject().toString();
            String nextPredicate = next.getPredicate().toString();

            if (nextPredicate.endsWith("row") || nextPredicate.endsWith("%20html%3E")) {

            } else if (nextPredicate.endsWith("#Station")) {
                station.setName(next.getObject().toString().replace(' ', '_'));
            } else if (nextPredicate.endsWith("#x")) {
                String object = next.getObject().toString();
                station.setX(Double.parseDouble(object.split("\"")[1]));
            } else if (nextPredicate.endsWith("#y")) {
                String object = next.getObject().toString();
                station.setY(Double.parseDouble(object.split("\"")[1]));
            } else if (nextPredicate.endsWith("#Divisions")) {
                String divisionName = next.getObject().toString().replace(' ', '_');
                if (!Divisions.contains(divisionName)) {
                    division.setName(divisionName);
                    station.setDivision(division);
                }
            } else if (nextPredicate.endsWith("#id")) {
            } else {
                if (nextPredicate.endsWith("Attempts%20or%20threats%20to%20murder%2C%20assaults%2C%20harassments%20and%20related%20offences%202015")) {
                    int counter = Integer.parseInt(next.getObject().toString().split("\"")[1]);
                    crime.setMuderCount(counter);
                } else if (nextPredicate.endsWith("Burglary%20and%20related%20offences%202015")) {
                    int counter = Integer.parseInt(next.getObject().toString().split("\"")[1]);
                    crime.setBurglaryCount(counter);
                } else if (nextPredicate.endsWith("Controlled%20drug%20offences%202015")) {
                    int counter = Integer.parseInt(next.getObject().toString().split("\"")[1]);
                    crime.setDrugCount(counter);
                } else if (nextPredicate.endsWith("Damage%20to%20property%20and%20to%20the%20environment%202015")) {
                    int counter = Integer.parseInt(next.getObject().toString().split("\"")[1]);
                    crime.setPropertyCount(counter);
                } else if (nextPredicate.endsWith("Dangerous%20or%20negligent%20acts%202015")) {
                    int counter = Integer.parseInt(next.getObject().toString().split("\"")[1]);
                    crime.setDangerousCount(counter);
                } else if (nextPredicate.endsWith("Fraud%2C%20deception%20and%20related%20offences%202015")) {
                    int counter = Integer.parseInt(next.getObject().toString().split("\"")[1]);
                    crime.setFraudCount(counter);
                } else if (nextPredicate.endsWith("Kidnapping%20and%20related%20offences%202015")) {
                    int counter = Integer.parseInt(next.getObject().toString().split("\"")[1]);
                    crime.setKidnappingCount(counter);
                } else if (nextPredicate.endsWith("Offences%20against%20government%2C%20justice%20procedures%20and%20organisation%20of%20crime%202015")) {
                    int counter = Integer.parseInt(next.getObject().toString().split("\"")[1]);
                    crime.setGovernmentCount(counter);
                } else if (nextPredicate.endsWith("Public%20order%20and%20other%20social%20code%20offences%202015")) {
                    int counter = Integer.parseInt(next.getObject().toString().split("\"")[1]);
                    crime.setPublicCount(counter);
                } else if (nextPredicate.endsWith("Robbery%2C%20extortion%20and%20hijacking%20offences%202015")) {
                    int counter = Integer.parseInt(next.getObject().toString().split("\"")[1]);
                    crime.setRobberyCount(counter);
                } else if (nextPredicate.endsWith("Theft%20and%20related%20offences%202015")) {
                    int counter = Integer.parseInt(next.getObject().toString().split("\"")[1]);
                    crime.setTheftCount(counter);
                } else if (nextPredicate.endsWith("Weapons%20and%20Explosives%20Offences%202015")) {
                    int counter = Integer.parseInt(next.getObject().toString().split("\"")[1]);
                    crime.setWeponsCount(counter);
                }

            }
            // Do something with each triple

        }
    }

    public static void writeToFile(String filename)
            throws FileNotFoundException {
        try {
            ontology.write(new FileOutputStream(new File(filename)),
                    "RDF/XML-ABBREV");
            System.out.println("Ontology written to file.");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
