import { useState, useEffect } from "react";
import type { SearchContextStructure } from "../contexts/RegistrySearch/SearchContextStructure";
import { useNavigate } from "react-router";
function useNPMFetch(queriedSearch: SearchContextStructure|null){
    //const [queryResult, setQueryResult] = useState(null);
    const [loading, setLoading] = useState<boolean>(false);
    const [fetchError, setFetchError] = useState<string|null|Error>(null);
    const [packageData, setPackageData] = useState<JSON|null>(null);

    const navigate = useNavigate();
    useEffect(() => {
        if(!queriedSearch){
            return;
        }
        const fetchFromBackend = async () => {
            setLoading(true);
            try{
                let response = null;
                if(queriedSearch.search_term.length == 2){
                    navigate(`/package/${queriedSearch.ecosystem}/${queriedSearch.search_term[0]}/${queriedSearch.search_term[1]}`);
                    response = await fetch(`/bend/api/package/${queriedSearch.ecosystem}/${queriedSearch.search_term[0]}/${queriedSearch.search_term[1]}`);
                }
                else if(queriedSearch.search_term.length == 1){
                    navigate(`/package/${queriedSearch.ecosystem}/${queriedSearch.search_term[0]}`);
                    response = await fetch(`/bend/api/package/${queriedSearch.ecosystem}/${queriedSearch.search_term[0]}`);
                }

                if(response != null){
                    console.log(response.status);
                    const info = await response.json();
                    setPackageData(info);
                }
                else{
                    setFetchError("Response was empty");
                }
            }
            catch(reponseError: unknown){
                if(reponseError instanceof Error){
                    setFetchError("fetchFromBackend: " + reponseError);
                }
                else{
                    setFetchError("Unkown Error");
                }
            }
            setLoading(false);
        }
        fetchFromBackend();
    }, [queriedSearch])
    return {packageData, loading, fetchError}
}
export default useNPMFetch;