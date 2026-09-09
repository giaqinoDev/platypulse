import { useState } from "react";
import { SnapShotsTimelineSchema, type SnapShotTimeline } from "./SnapShot";
function useFetchSnapShots(){
    const [loading, setLoading] = useState<boolean>(false);
    const [fetchError, setFetchError] = useState<string|null|Error>(null);
    const [fetchedSnapShots, setFetchedSnapShots] = useState<SnapShotTimeline|null>();

    async function getSnapShots(packageId: string|null|undefined){

        if(!packageId){
            return;
        }
        setLoading(true);

        try{
            let response = null;
            response = await fetch(`/bend/api/package/${packageId}/snapshots`);
            if(response.status == 200){
                const data = await response.json();
                const snapshots = SnapShotsTimelineSchema.parse(data);
                setFetchedSnapShots(snapshots);
            }
            else{
                setFetchError("Response was empty");
            }
        }
        catch(responseError: unknown){
            if(responseError instanceof Error){
                setFetchError("Backend Error: " + responseError);
            }
            else{
                setFetchError("Unkown Error");
            }
        }
        setLoading(false);
        return fetchedSnapShots;
    }

    return {loading, getSnapShots, fetchError}
}

export default useFetchSnapShots;