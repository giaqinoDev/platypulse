import { useNavigate } from "react-router";
import type { SearchContextStructure } from "../../contexts/RegistrySearch/SearchContextStructure";
import "./SnapShots.css"
import { HiOutlineDocumentReport } from "react-icons/hi";


interface SnapShotsProps {
    fetchedSnapShots: {
        snap_shot_id: string;
        overall_score: number;
        created_at: Date;
    }[]|null
    query: SearchContextStructure|null
}

function SnapShots({ fetchedSnapShots, query }: SnapShotsProps){
    const navigate = useNavigate();
    if(!fetchedSnapShots){
        return;
    }

    function handleOnClick(snapshotId: string){
        if(!query){
            return
        }

        if(query?.search_term.length == 2){
            navigate(`/package/${query.ecosystem}/${query.search_term[0].replace("@", "")}/${query.search_term[1]}/id/${snapshotId}`);
        }
        else if(query?.search_term.length == 1){
            navigate(`/package/${query.ecosystem}/${query.search_term[0]}/id/${snapshotId}`);
        }
    }
    return(
        <div>
            {fetchedSnapShots?.map((snapshot) => (
                <div key={snapshot.snap_shot_id} className="snap-shot-entry" onClick={() => {handleOnClick(snapshot.snap_shot_id)}}>
                    <HiOutlineDocumentReport className="report-icon"/>
                    <p className="snap-shot-detail" id="creation-date-time">Created {snapshot.created_at.toDateString()}, {snapshot.created_at.toLocaleTimeString()}</p>
                    <p className="snap-shot-detail" id="overall-score">{snapshot.overall_score}%</p>
                </div>
            ))}
        </div>
    );
}

export default SnapShots;