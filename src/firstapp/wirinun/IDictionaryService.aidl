package firstapp.wirinun;


interface IDictionaryService {

	List<String> getLanguageBMeaning(in String word);
	List<String> getLanguageAMeaning(in String word);
	List<String> getLanguageBMeaningInContext(in String word, in String context);
	List<String> getLanguageAMeaningInContext(in String word, in String context);

}