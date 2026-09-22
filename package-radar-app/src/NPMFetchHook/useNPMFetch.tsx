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
            console.log("NPM FETCH EFFECT", queriedSearch);
            setLoading(true);
            let api_root = `/bend/api/package/${queriedSearch.ecosystem}/${queriedSearch.search_term[0]}`;
            let api_branch = `/latest`;
            try{
                let response = null;
                if(queriedSearch.search_term.length == 2){
                    api_root = `/bend/api/package/${queriedSearch.ecosystem}/${queriedSearch.search_term[0]}/${queriedSearch.search_term[1]}`;
                }
                if(queriedSearch.reportId !== undefined &&  queriedSearch.reportId !== null){
                    console.log("INSIDE IF: " + queriedSearch.reportId);
                    api_branch = `/id/${queriedSearch.reportId}`;
                }


                response = await fetch(api_root + api_branch);
                if(response?.status == 200){
                    try{
                        console.log(response?.status);
                        const info = await response.json();
                        const packageData = NpmFetchResponseSchema.parse(info);
                        console.log(packageData);
                        
                        setPackageData(packageData);
                    }
                    catch(error){
                        console.log("Zod Error:", error)
                    }
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