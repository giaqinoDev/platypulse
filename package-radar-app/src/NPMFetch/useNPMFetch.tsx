import { useState, useEffect } from "react";
import type { SearchContextStructure } from "../contexts/RegistrySearch/SearchContextStructure";
import { NpmFetchResponseSchema, type NpmFetchResponse } from "./NpmFetchResponse";

function useNPMFetch(queriedSearch: SearchContextStructure|null){
    //const [queryResult, setQueryResult] = useState(null);
    const [loading, setLoading] = useState<boolean>(false);
    const [fetchError, setFetchError] = useState<string|null|Error>(null);
    const [packageData, setPackageData] = useState<NpmFetchResponse | null>(null);
    useEffect(() => {

        if(!queriedSearch){
            return;
        }

        const fetchFromBackend = async () => {
            setLoading(true);
            try{
                let response = null;
                if(queriedSearch.search_term.length == 2){
                    response = await fetch(`/bend/api/package/${queriedSearch.ecosystem}/${queriedSearch.search_term[0]}/${queriedSearch.search_term[1]}/latest`);
                }
                else if(queriedSearch.search_term.length == 1){
                    response = await fetch(`/bend/api/package/${queriedSearch.ecosystem}/${queriedSearch.search_term[0]}/latest`);
                }

                if(response?.status == 200){
                    console.log(response?.status);
                    const info = await response?.json();
                    const packageData = NpmFetchResponseSchema.parse(info);
                    setPackageData(packageData);
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