
App:compile
	mvn exec:java -Dexec.mainClass="tk.luoxing123.App"
App2:compile
	mvn exec:java -Dexec.mainClass="tk.luoxing123.App2"
InNewswire:compile
	mvn  exec:java -Dexec.mainClass="tk.luoxing123.corpus.InNewswire"
ArticleCollection:compile
	mvn  exec:java -Dexec.mainClass="tk.luoxing123.corpus.ArticleCollection"
QueryCollection:compile
	mvn  exec:java -Dexec.mainClass="tk.luoxing123.corpus.QueryCollection"
InferenceEngine:compile
	mvn  exec:java -Dexec.mainClass="tk.luoxing123.entitylink.InferenceEngine"
Mentions:
	mvn exec:java -Dexec.mainClass="tk.luoxing123.app.MakeMentionIndex"
compile:
	mvn compile
MentionFactory:compile
	mvn exec:java -Dexec.mainClass="tk.luoxing123.entitylink.MentionFactory"
trainCollection:compile
	mvn  exec:java -Dexec.mainClass="tk.luoxing123.corpus.trainCollection"
Graph:compile
	mvn  exec:java -Dexec.mainClass="tk.luoxing123.graph.Graph"
goldCollection:compile
	mvn exec:java -Dexec.mainClass="tk.luoxing123.corpus.goldCollection"
La4j:compile
	mvn exec:java -Dexec.mainClass="tk.luoxing123.test.La4j"
clean:
	mvn clean
